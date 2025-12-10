/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.VideoItemModel
import com.andannn.melodify.core.player.util.EXTRA_ALBUM_COVER_ART_KEY
import com.andannn.melodify.core.player.util.IS_AUDIO_KEY
import com.andannn.melodify.core.player.util.UNIQUE_ID_KEY
import com.andannn.melodify.core.player.util.VIDEO_BUCKET_NAME
import com.andannn.melodify.core.player.util.VIDEO_HEIGHT_KEY
import com.andannn.melodify.core.player.util.VIDEO_WIDTH_KEY
import com.andannn.melodify.core.player.util.buildMediaItem

fun MediaItem.toAppItem(): MediaItemModel =
    if (mediaMetadata.extras?.getBoolean(IS_AUDIO_KEY) == true) {
        AudioItemModel(
            id = mediaId,
            name = mediaMetadata.title.toString(),
            modifiedDate = 0,
            album = mediaMetadata.albumTitle.toString(),
            albumId = "0",
            artist = mediaMetadata.artist.toString(),
            artistId = "0",
            genre = mediaMetadata.genre.toString(),
            genreId = "0",
            cdTrackNumber = mediaMetadata.trackNumber ?: 0,
            discNumber = 0,
            artWorkUri =
                mediaMetadata.artworkUri?.toString() ?: mediaMetadata.extras?.getString(
                    EXTRA_ALBUM_COVER_ART_KEY,
                ) ?: "",
            extraUniqueId = mediaMetadata.extras?.getString(UNIQUE_ID_KEY),
            releaseYear = "0",
            path = "",
            source =
                Uri
                    .withAppendedPath(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        mediaId,
                    ).toString(),
        )
    } else {
        VideoItemModel(
            id = mediaId,
            name = mediaMetadata.title.toString(),
            modifiedDate = 0,
            artWorkUri = "",
            bucketId = "",
            width = mediaMetadata.extras?.getInt(VIDEO_WIDTH_KEY) ?: 0,
            height = mediaMetadata.extras?.getInt(VIDEO_HEIGHT_KEY) ?: 0,
            bucketName = mediaMetadata.extras?.getString(VIDEO_BUCKET_NAME).orEmpty(),
            path = "",
            duration = 0,
            size = 0,
            mimeType = "",
            resolution = "",
            relativePath = "",
            source =
                Uri
                    .withAppendedPath(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        mediaId,
                    ).toString(),
            extraUniqueId = mediaMetadata.extras?.getString(UNIQUE_ID_KEY),
        )
    }

private var counter = 1

private val uniqueId get() = "media_item_unique_id" + counter++

fun MediaItemModel.toMediaItem(generateUniqueId: Boolean = false): MediaItem =
    when (this) {
        is AudioItemModel -> toMediaItem(generateUniqueId)
        is VideoItemModel -> toMediaItem(generateUniqueId)
        else -> error("Not support")
    }

fun VideoItemModel.toMediaItem(generateUniqueId: Boolean = false): MediaItem =
    buildMediaItem(
        isAudio = false,
        mediaId = id,
        title = name,
        sourceUri = source.toUri(),
        imageUri = artWorkUri?.toUri(),
        videoWidth = width,
        videoHeight = height,
        isPlayable = true,
        isBrowsable = false,
        mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
        uniqueId = if (generateUniqueId) uniqueId else null,
    )

fun AudioItemModel.toMediaItem(generateUniqueId: Boolean = false): MediaItem =
    buildMediaItem(
        isAudio = true,
        mediaId = id,
        title = name,
        sourceUri = source.toUri(),
        imageUri = artWorkUri?.toUri(),
        trackNumber = cdTrackNumber,
        album = album,
        artist = artist,
        isPlayable = true,
        isBrowsable = false,
        mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
        uniqueId = if (generateUniqueId) uniqueId else null,
    )
