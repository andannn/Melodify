/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

/**
 * group key for media items
 */
sealed interface GroupKey {
    data class Artist(
        val artistId: String,
    ) : GroupKey

    data class Album(
        val albumId: String,
    ) : GroupKey

    data class Genre(
        val genreId: String,
    ) : GroupKey

    data class Year(
        val year: String,
    ) : GroupKey

    data class Title(
        val firstCharacterString: String,
    ) : GroupKey
}

fun AudioItemModel.keyOf(sortOption: SortOption): GroupKey? =
    when (sortOption) {
        is SortOption.Album -> GroupKey.Album(albumId)
        is SortOption.Artist -> GroupKey.Artist(artistId)
        is SortOption.Genre -> GroupKey.Genre(genreId)
        is SortOption.ReleaseYear -> GroupKey.Year(releaseYear)
        is SortOption.Title -> GroupKey.Title(name[0].toString())
        SortOption.NONE -> null
        is SortOption.TrackNum -> error("Not support")
    }
