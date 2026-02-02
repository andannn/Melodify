/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

sealed class Tab(
    open val tabId: Long,
) {
    data class AllMusic(
        override val tabId: Long,
    ) : Tab(tabId)

    data class AllVideo(
        override val tabId: Long,
    ) : Tab(tabId)

    data class AlbumDetail(
        override val tabId: Long,
        val albumId: String,
        val label: String,
    ) : Tab(tabId)

    data class ArtistDetail(
        override val tabId: Long,
        val artistId: String,
        val label: String,
    ) : Tab(tabId)

    data class GenreDetail(
        override val tabId: Long,
        val genreId: String,
        val label: String,
    ) : Tab(tabId)

    data class PlayListDetail(
        override val tabId: Long,
        val playListId: String,
        val label: String,
    ) : Tab(tabId)

    data class BucketDetail(
        override val tabId: Long,
        val bucketId: String,
        val label: String,
    ) : Tab(tabId)
}

enum class TabKind {
    ALBUM,
    ARTIST,
    GENRE,
    PLAYLIST,
    ALL_MUSIC,
    ALL_VIDEO,
    VIDEO_BUCKET,
}
