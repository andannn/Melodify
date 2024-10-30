package com.andannn.melodify.core.data.repository

import android.net.Uri
import android.provider.MediaStore
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.player.library.mediastore.MediaStoreSource
import com.andannn.melodify.core.player.library.mediastore.model.AudioData
import org.koin.mp.KoinPlatform.getKoin

actual suspend fun getMediaListFromIds(mediaStoreIds: List<String>): List<AudioItemModel> {
    val mediaSource = getKoin().get<MediaStoreSource>().getAudioByIds(mediaStoreIds)
    return mediaSource.map { it.toAppItem() }
}

private fun AudioData.toAppItem() = AudioItemModel(
    id = id.toString(),
    name = title,
    modifiedDate = modifiedDate,
    artWorkUri = Uri.withAppendedPath(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        albumId.toString(),
    ).toString(),
    album = album ?: "",
    albumId = albumId.toString(),
    artist = artist.toString(),
    artistId = artistId.toString(),
    cdTrackNumber = cdTrackNumber ?: 0,
    discNumberIndex = discNumber ?: 0,
)