/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.model.AudioItemModel
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

    override suspend fun deleteMedias(mediaList: List<AudioItemModel>): MediaFileDeleteHelper.Result =
        mutex.withLock {
            val completer = CompletableDeferred<ActivityResult>()
            currentCompleter = completer

            try {
                val ids = mediaList.map { itemModel -> itemModel.id }
                val uriList =
                    ids.map { id ->
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id.toLong(),
                        )
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
                intentSenderLauncher?.launch(request) ?: error("intentSender not set.")
                val result = completer.await()
                if (result.resultCode == Activity.RESULT_OK) {
                    Napier.d(tag = TAG) { "delete success" }
                    val paths =
                        mediaList
                            .mapNotNull { item ->
                                item.path.let { File(it).parentFile?.path }
                            }.distinct()
                            .toTypedArray()

                    // Mark file as deleted.
                    mediaLibraryRepository.markMediaAsDeleted(ids)

                    // Side Effect to re-scan MediaStore.
                    Napier.d(tag = TAG) { "scan start ${paths.toList()}" }
                    MediaScannerConnection.scanFile(
                        context,
                        paths,
                        arrayOf("audio/*"),
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
