/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

sealed interface SortOption {
    data class Album(
        val ascending: Boolean,
    ) : SortOption

    data class Title(
        val ascending: Boolean,
    ) : SortOption

    data class Artist(
        val ascending: Boolean,
    ) : SortOption

    data class TrackNum(
        val ascending: Boolean,
    ) : SortOption

    data class Genre(
        val ascending: Boolean,
    ) : SortOption

    data class ReleaseYear(
        val ascending: Boolean,
    ) : SortOption

    data object NONE : SortOption
}

fun SortOption.isAscending() =
    when (this) {
        is SortOption.Album -> ascending
        is SortOption.Artist -> ascending
        is SortOption.Title -> ascending
        is SortOption.TrackNum -> ascending
        is SortOption.Genre -> ascending
        is SortOption.ReleaseYear -> ascending
        is SortOption.NONE -> false
    }
