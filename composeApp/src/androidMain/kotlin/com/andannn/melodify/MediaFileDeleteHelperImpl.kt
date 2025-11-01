package com.andannn.melodify

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.andannn.melodify.core.data.model.MediaItemModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "MediaFileDeleteHelper"

class MediaFileDeleteHelperImpl(
    context: Context,
    private val intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
) : MediaFileDeleteHelper {
    private val resolver = context.contentResolver

    private var currentCompleter: CompletableDeferred<ActivityResult>? = null
    private val mutex = Mutex()

    override suspend fun deleteMedias(mediaList: List<MediaItemModel>) =
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
                intentSenderLauncher.launch(request)
                val result = completer.await()
                if (result.resultCode == Activity.RESULT_OK) {
                    Napier.d(tag = TAG) { "delete success" }
                    MediaScannerConnection.scanFile()
                }
            } finally {
                currentCompleter = null
            }
        }

    fun onResult(result: ActivityResult) {
        Napier.d(tag = TAG) { "activity result: $result" }
        currentCompleter?.complete(result)
    }
}
