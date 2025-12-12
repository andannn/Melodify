/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

sealed class CustomTab(
    open val tabId: Long,
) {
    data class AllMusic(
        override val tabId: Long,
    ) : CustomTab(tabId)

    data class AllVideo(
        override val tabId: Long,
    ) : CustomTab(tabId)

    data class AlbumDetail(
        override val tabId: Long,
        val albumId: String,
        val label: String,
    ) : CustomTab(tabId)

    data class ArtistDetail(
        override val tabId: Long,
        val artistId: String,
        val label: String,
    ) : CustomTab(tabId)

    data class GenreDetail(
        override val tabId: Long,
        val genreId: String,
        val label: String,
    ) : CustomTab(tabId)

    data class PlayListDetail(
        override val tabId: Long,
        val playListId: String,
        val label: String,
        val isAudio: Boolean,
    ) : CustomTab(tabId)

    data class BucketDetail(
        override val tabId: Long,
        val bucketId: String,
        val label: String,
    ) : CustomTab(tabId)
}

fun CustomTab.isAudio() =
    when (this) {
        is CustomTab.AlbumDetail,
        is CustomTab.AllMusic,
        is CustomTab.ArtistDetail,
        is CustomTab.GenreDetail,
        -> true

        is CustomTab.AllVideo,
        is CustomTab.BucketDetail,
        -> false

        is CustomTab.PlayListDetail -> isAudio
    }

enum class TabKind {
    ALBUM,
    ARTIST,
    GENRE,
    AUDIO_PLAYLIST,
    VIDEO_PLAYLIST,
    ALL_MUSIC,
    ALL_VIDEO,
    VIDEO_BUCKET,
}
