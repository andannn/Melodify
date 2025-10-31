/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.database.MediaSorts
import com.andannn.melodify.core.database.Sort
import com.andannn.melodify.core.database.SortOrder
import com.andannn.melodify.core.database.entity.MediaColumns

enum class PresetSortRule(
    val sortRule: SortRule,
) {
    AlbumAsc(
        sortRule = SortRule.Preset.AlbumASC,
    ),
    ArtistAsc(
        sortRule = SortRule.Preset.ArtistASC,
    ),
    TitleNameAsc(
        sortRule = SortRule.Preset.TitleASC,
    ),
    ArtistAlbumASC(
        sortRule = SortRule.Preset.ArtistAlbumASC,
    ),
}

data class SortRule(
    val primaryGroupSort: SortOption,
    val secondaryGroupSort: SortOption = SortOption.NONE,
    val contentSort: SortOption = SortOption.NONE,
    val showTrackNum: Boolean = false,
    val isPreset: Boolean = true,
) {
    companion object Preset {
        val TitleASC =
            SortRule(
                primaryGroupSort = SortOption.Title(true),
                showTrackNum = false,
                isPreset = true,
            )
        val AlbumASC =
            SortRule(
                primaryGroupSort = SortOption.Album(true),
                contentSort = SortOption.TrackNum(true),
                showTrackNum = true,
                isPreset = true,
            )
        val ArtistASC =
            SortRule(
                primaryGroupSort = SortOption.Artist(true),
                showTrackNum = false,
                isPreset = true,
            )

        val ArtistAlbumASC =
            SortRule(
                primaryGroupSort = SortOption.Artist(true),
                secondaryGroupSort = SortOption.Album(true),
                contentSort = SortOption.TrackNum(true),
                showTrackNum = true,
                isPreset = true,
            )

        val DefaultPreset = AlbumASC
        val DefaultCustom = AlbumASC.copy(isPreset = false)
    }
}

internal fun SortRule.toSortMethod() =
    MediaSorts.buildMethod {
        addSortOption(primaryGroupSort)
        addSortOption(secondaryGroupSort)
        addSortOption(contentSort)
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

internal fun Boolean.toOrder() = if (this) SortOrder.ASCENDING else SortOrder.DESCENDING
