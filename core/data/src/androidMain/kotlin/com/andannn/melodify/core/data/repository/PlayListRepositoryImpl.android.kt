package com.andannn.melodify.core.data.repository

import android.net.Uri
import android.provider.MediaStore
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.player.library.mediastore.MediaStoreSource
import com.andannn.melodify.core.player.library.mediastore.model.AudioData
import org.koin.mp.KoinPlatform.getKoin

actual suspend fun getMediaListFromIds(playListItems: List<PlayListWithMediaCrossRef>): List<AudioItemModel> {
    val audioDataList =
        getKoin().get<MediaStoreSource>().getAudioByIds(playListItems.map { it.mediaStoreId })
    return playListItems.map {
        audioDataList.firstOrNull { audioData -> audioData.id.toString() == it.mediaStoreId }
            ?.toAppItem()
            ?: AudioItemModel(
                id = AudioItemModel.INVALID_ID_PREFIX + it.mediaStoreId,
                name = it.title,
                artist = it.artist,
                modifiedDate = 0,
                artWorkUri = "",
                album = "",
                albumId = "",
                artistId = "",
                cdTrackNumber = 0,
                discNumberIndex = 0,
            )
    }
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