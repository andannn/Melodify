/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.database.MediaSorts
import com.andannn.melodify.core.database.Sort
import com.andannn.melodify.core.database.SortOrder
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.VideoColumns

sealed interface SortOption {
    sealed interface AudioOption : SortOption {
        data class Album(
            val ascending: Boolean,
        ) : AudioOption

        data class Artist(
            val ascending: Boolean,
        ) : AudioOption

        data class TrackNum(
            val ascending: Boolean,
        ) : AudioOption

        data class Genre(
            val ascending: Boolean,
        ) : AudioOption

        data class ReleaseYear(
            val ascending: Boolean,
        ) : AudioOption

        data class Title(
            val ascending: Boolean,
        ) : AudioOption
    }

    sealed interface VideoOption : SortOption {
        data class Bucket(
            val ascending: Boolean,
        ) : VideoOption

        data class Title(
            val ascending: Boolean,
        ) : VideoOption
    }

    data object NONE : SortOption
}

fun SortOption.isAscending() =
    when (this) {
        is SortOption.AudioOption.Album -> ascending
        is SortOption.AudioOption.Artist -> ascending
        is SortOption.AudioOption.TrackNum -> ascending
        is SortOption.AudioOption.Genre -> ascending
        is SortOption.AudioOption.ReleaseYear -> ascending
        is SortOption.AudioOption.Title -> ascending
        is SortOption.VideoOption.Bucket -> ascending
        is SortOption.VideoOption.Title -> ascending
        is SortOption.NONE -> false
    }

internal fun List<SortOption.AudioOption>.toAudioSortMethod() =
    MediaSorts.buildMethod {
        this@toAudioSortMethod.forEach {
            addAudioSortOption(it)
        }
    }

internal fun List<SortOption.VideoOption>.toVideoSortMethod() =
    MediaSorts.buildMethod {
        this@toVideoSortMethod.forEach {
            addAudioSortOption(it)
        }
    }

private fun MutableList<Sort>.addAudioSortOption(sort: SortOption) {
    when (sort) {
        is SortOption.AudioOption.Album ->
            apply {
                add(Sort(MediaColumns.ALBUM, sort.ascending.toOrder()))
            }

        is SortOption.AudioOption.Artist ->
            apply {
                add(Sort(MediaColumns.ARTIST, sort.ascending.toOrder()))
            }

        is SortOption.AudioOption.Title ->
            apply {
                add(Sort(MediaColumns.TITLE, sort.ascending.toOrder()))
            }

        is SortOption.AudioOption.TrackNum ->
            apply {
                add(Sort(MediaColumns.CD_TRACK_NUMBER, sort.ascending.toOrder()))
            }

        is SortOption.AudioOption.Genre ->
            apply {
                add(Sort(MediaColumns.GENRE, sort.ascending.toOrder()))
            }

        is SortOption.AudioOption.ReleaseYear ->
            apply {
                add(Sort(MediaColumns.YEAR, sort.ascending.toOrder()))
            }

        is SortOption.VideoOption.Bucket ->
            apply {
                add(Sort(VideoColumns.BUCKET_DISPLAY_NAME, sort.ascending.toOrder()))
            }

        is SortOption.VideoOption.Title ->
            apply {
                add(Sort(VideoColumns.TITLE, sort.ascending.toOrder()))
            }

        SortOption.NONE -> apply { }
    }
}

private fun Boolean.toOrder() = if (this) SortOrder.ASCENDING else SortOrder.DESCENDING
