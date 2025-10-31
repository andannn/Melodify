/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.database.MediaSorts
import com.andannn.melodify.core.database.Sort
import com.andannn.melodify.core.database.SortOrder
import com.andannn.melodify.core.database.entity.MediaColumns

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

internal fun List<SortOption>.toSortMethod() =
    MediaSorts.buildMethod {
        this@toSortMethod.forEach {
            addSortOption(it)
        }
    }

internal fun MutableList<Sort>.addSortOption(sort: SortOption) {
    when (sort) {
        is SortOption.Album ->
            apply {
                add(Sort(MediaColumns.ALBUM, sort.ascending.toOrder()))
            }

        is SortOption.Artist ->
            apply {
                add(Sort(MediaColumns.ARTIST, sort.ascending.toOrder()))
            }

        is SortOption.Title ->
            apply {
                add(Sort(MediaColumns.TITLE, sort.ascending.toOrder()))
            }

        is SortOption.TrackNum ->
            apply {
                add(Sort(MediaColumns.CD_TRACK_NUMBER, sort.ascending.toOrder()))
            }

        is SortOption.Genre ->
            apply {
                add(Sort(MediaColumns.GENRE, sort.ascending.toOrder()))
            }

        is SortOption.ReleaseYear ->
            apply {
                add(Sort(MediaColumns.YEAR, sort.ascending.toOrder()))
            }

        SortOption.NONE -> {}
    }
}

private fun Boolean.toOrder() = if (this) SortOrder.ASCENDING else SortOrder.DESCENDING
