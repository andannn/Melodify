package com.andannn.melodify.core.data.repository

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.util.uri
import com.andannn.melodify.core.player.util.UNIQUE_ID_KEY
import com.andannn.melodify.core.player.util.buildMediaItem

fun MediaItem.toAppItem(): MediaItemModel = AudioItemModel(
    id = mediaId,
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

private var counter = 1

private val uniqueId get() = "media_item_unique_id" + counter++

fun AudioItemModel.toMediaItem(generateUniqueId: Boolean = false): MediaItem {
    return buildMediaItem(
        mediaId = id,
        title = name,
        sourceUri = Uri.parse(uri),
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
