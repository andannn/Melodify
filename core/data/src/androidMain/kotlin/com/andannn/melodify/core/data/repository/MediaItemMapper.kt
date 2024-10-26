package com.andannn.melodify.core.data.repository

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.player.library.LibraryRootCategory
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.util.uri
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.player.library.PLAYABLE_MEDIA_ITEM_PREFIX
import com.andannn.melodify.core.player.util.UNIQUE_ID_KEY
import com.andannn.melodify.core.player.util.buildMediaItem

fun MediaItem.toAppItem(): MediaItemModel = when {
    mediaId.contains(PLAYABLE_MEDIA_ITEM_PREFIX) -> AudioItemModel(
        id = mediaId.substringAfter(PLAYABLE_MEDIA_ITEM_PREFIX),
        name = mediaMetadata.title.toString(),
        modifiedDate = 0,
        album = mediaMetadata.albumTitle.toString(),
        albumId = "0",
        artist = mediaMetadata.artist.toString(),
        artistId = "0",
        cdTrackNumber = mediaMetadata.trackNumber ?: 0,
        discNumberIndex = 0,
        artWorkUri = mediaMetadata.artworkUri.toString(),
        extraUniqueId = mediaMetadata.extras?.getString(UNIQUE_ID_KEY)
    )

    mediaId.contains(LibraryRootCategory.ALBUM.childrenPrefix) -> AlbumItemModel(
        id = mediaId.substringAfter(LibraryRootCategory.ALBUM.childrenPrefix),
        name = mediaMetadata.title.toString(),
        trackCount = mediaMetadata.totalTrackCount ?: 0,
        artWorkUri = mediaMetadata.artworkUri.toString()
    )

    mediaId.contains(LibraryRootCategory.ARTIST.childrenPrefix) -> ArtistItemModel(
        id = mediaId.substringAfter(LibraryRootCategory.ARTIST.childrenPrefix),
        name = mediaMetadata.title.toString(),
        trackCount = mediaMetadata.totalTrackCount ?: 0,
        artWorkUri = mediaMetadata.artworkUri.toString()
    )

    mediaId.contains(LibraryRootCategory.GENRE.childrenPrefix) -> {
        val id = mediaId.substringAfter(LibraryRootCategory.GENRE.childrenPrefix)
        GenreItemModel(
            id = id,
            name = mediaMetadata.title.toString(),
            trackCount = mediaMetadata.totalTrackCount ?: 0,
            artWorkUri = mediaMetadata.artworkUri.toString()
        )
    }

    else -> error("Not a AppMediaItem $this")
}

fun PlayListWithMediaCount.toAppItem() = PlayListItemModel(
    id = playListEntity.id.toString(),
    name = playListEntity.name,
    artWorkUri = playListEntity.artworkUri ?: "",
    trackCount = mediaCount
)

private var counter = 1

private val uniqueId get() = "media_item_unique_id" + counter++

fun AudioItemModel.toMediaItem(generateUniqueId: Boolean = false): MediaItem {
    return buildMediaItem(
        title = name,
        sourceUri = Uri.parse(uri),
        mediaId = PLAYABLE_MEDIA_ITEM_PREFIX + id,
        imageUri = Uri.parse(artWorkUri),
        trackNumber = cdTrackNumber,
        album = album,
        artist = artist,
        isPlayable = true,
        isBrowsable = false,
        mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
        uniqueId = if (generateUniqueId) uniqueId else null
    )
}
