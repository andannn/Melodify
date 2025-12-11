/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.VideoItemModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import java.io.File

private const val TAG = "MediaFileDeleteHelper"

class MediaFileDeleteHelperImpl :
    MediaFileDeleteHelper,
    KoinComponent {
    var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>? = null

    private val mediaLibraryRepository: MediaContentRepository = getKoin().get()
    private val context: Context = getKoin().get()
    private val resolver = context.contentResolver
    private var currentCompleter: CompletableDeferred<ActivityResult>? = null
    private val mutex = Mutex()

    override suspend fun deleteMedias(mediaList: List<MediaItemModel>): MediaFileDeleteHelper.Result =
        mutex.withLock {
            val completer = CompletableDeferred<ActivityResult>()
            currentCompleter = completer

            try {
                val uriList =
                    mediaList.map { itemModel ->
                        when (itemModel) {
                            is AudioItemModel -> itemModel.source.toUri()
                            is VideoItemModel -> itemModel.source.toUri()
                            else -> error("Not supported")
                        }
                    }

                val pendingIntent =
                    MediaStore.createDeleteRequest(
                        resolver,
                        uriList,
                    )
                val request =
                    IntentSenderRequest
                        .Builder(pendingIntent.intentSender)
                        .build()
                Napier.d(tag = TAG) { "request delete uriList: $uriList" }
                intentSenderLauncher?.launch(request) ?: error("intentSender not set.")
                val result = completer.await()
                if (result.resultCode == Activity.RESULT_OK) {
                    Napier.d(tag = TAG) { "delete success" }
                    val paths =
                        mediaList
                            .mapNotNull { item ->
                                val path =
                                    when (item) {
                                        is AudioItemModel -> item.path
                                        is VideoItemModel -> item.path
                                        else -> error("Not support")
                                    }
                                path.let { File(it).parentFile?.path }
                            }.distinct()
                            .toTypedArray()

                    // Mark file as deleted.
                    mediaList.filterIsInstance<AudioItemModel>().let {
                        mediaLibraryRepository.markMediaAsDeleted(it.map { itemModel -> itemModel.id })
                    }
                    mediaList.filterIsInstance<VideoItemModel>().let {
                        mediaLibraryRepository.markVideoAsDeleted(it.map { itemModel -> itemModel.id })
                    }

                    // Side Effect to re-scan MediaStore.
                    Napier.d(tag = TAG) { "scan start ${paths.toList()}" }
                    MediaScannerConnection.scanFile(
                        context,
                        paths,
                        arrayOf("audio/*", "video/*"),
                    ) { path, uri ->
                        Napier.d(tag = TAG) { "scan finished. ${Thread.currentThread().name}" }
                    }
                    MediaFileDeleteHelper.Result.Success
                } else {
                    MediaFileDeleteHelper.Result.Denied
                }
            } catch (e: Exception) {
                Napier.e(tag = TAG, throwable = e) { "delete failed" }
                MediaFileDeleteHelper.Result.Failed
            } finally {
                currentCompleter = null
            }
        }

    fun onResult(result: ActivityResult) {
        Napier.d(tag = TAG) { "activity result: $result" }
        currentCompleter?.complete(result)
    }
}
