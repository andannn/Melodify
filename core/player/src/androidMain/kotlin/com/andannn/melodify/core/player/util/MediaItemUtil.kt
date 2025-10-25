/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.player.util

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata

const val UNIQUE_ID_KEY = "unique_id"
const val EXTRA_ALBUM_COVER_ART_KEY = "extra_album_cover_art_key"

fun buildMediaItem(
    title: String,
    mediaId: String,
    isPlayable: Boolean,
    isBrowsable: Boolean,
    mediaType: @MediaMetadata.MediaType Int,
    subtitleConfigurations: List<MediaItem.SubtitleConfiguration> = mutableListOf(),
    album: String? = null,
    artist: String? = null,
    genre: String? = null,
    sourceUri: Uri? = null,
    imageUri: Uri? = null,
    totalTrackCount: Int? = null,
    trackNumber: Int? = null,
    uniqueId: String? = null,
): MediaItem {
    val metadata =
        MediaMetadata
            .Builder()
            .setAlbumTitle(album)
            .setTitle(title)
            .setArtist(artist)
            .setGenre(genre)
            .setIsBrowsable(isBrowsable)
            .setIsPlayable(isPlayable)
// ISSUE: https://github.com/andannn/Melodify/issues/243
// Setting the album URI will cause a system UI issue when playing music.
// The AndroidX Media3 library automatically handles the notification artwork URI.
// A crash occurred when querying the album URI from MediaStore. This line is commented out for now.
//          .setArtworkUri(imageUri)
            .setMediaType(mediaType)
            .setTotalTrackCount(totalTrackCount)
            .setTrackNumber(trackNumber)
            .setExtras(
                Bundle().apply {
                    uniqueId?.let { putString(UNIQUE_ID_KEY, it) }
                    imageUri?.let { putString(EXTRA_ALBUM_COVER_ART_KEY, it.toString()) }
                },
            ).build()

    val requestMetadata =
        RequestMetadata
            .Builder()
            .setMediaUri(sourceUri)
            .build()

    return MediaItem
        .Builder()
        .setMediaId(mediaId)
        .setSubtitleConfigurations(subtitleConfigurations)
        .setMediaMetadata(metadata)
        .setUri(sourceUri)
        .setRequestMetadata(requestMetadata)
        .build()
}
