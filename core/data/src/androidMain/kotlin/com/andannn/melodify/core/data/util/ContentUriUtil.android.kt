package com.andannn.melodify.core.data.util

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import com.andannn.melodify.core.data.model.AudioItemModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.mp.KoinPlatform.getKoin

actual val AudioItemModel.uri: String
    get() = Uri.withAppendedPath(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        id
    ).toString()

actual fun contentChangedEventFlow(uri: String): Flow<Unit> {
    val contentResolver =
        getKoin().get<Context>().contentResolver ?: error("ContentResolver is null")
    return callbackFlow {
        val observer =
            object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean) {
                    trySend(Unit)
                }
            }

        contentResolver.registerContentObserver(
            /* uri = */ Uri.parse(uri),
            /* notifyForDescendants = */ true,
            /* observer = */ observer,
        )

        trySend(Unit)

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }
}

actual fun allAudioChangedEventFlow(): Flow<Unit> {
    return contentChangedEventFlow(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString())
}