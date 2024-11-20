package com.andannn.melodify.core.data.repository

import android.net.Uri
import android.provider.MediaStore
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.library.mediastore.MediaLibrary
import com.andannn.melodify.core.library.mediastore.model.AlbumData
import com.andannn.melodify.core.library.mediastore.model.ArtistData
import com.andannn.melodify.core.library.mediastore.model.AudioData
import com.andannn.melodify.core.library.mediastore.model.GenreData
import org.koin.mp.KoinPlatform.getKoin

// TODO: remove this function
actual suspend fun getMediaListFromIds(playListItems: List<PlayListWithMediaCrossRef>): List<AudioItemModel> {
    val audioDataList =
        getKoin().get<MediaLibrary>().getAudioByIds(playListItems.map { it.mediaStoreId })
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

// TODO: remove this function
fun AudioData.toAppItem() = AudioItemModel(
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

// TODO: remove this function
fun AlbumData.toAppItem() = AlbumItemModel(
    id = albumId.toString(),
    name = title,
    artWorkUri = Uri.withAppendedPath(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        albumId.toString(),
    ).toString(),
    trackCount = trackCount ?: 0,
)

// TODO: remove this function
fun ArtistData.toAppItem() = ArtistItemModel(
    id = artistId.toString(),
    name = name,
    artWorkUri = "",
    trackCount = trackCount,
)

fun GenreData.toAppItem() = GenreItemModel(
    id = genreId.toString(),
    name = name ?: "",
    artWorkUri = "",
    trackCount = 0,
)