/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

data class DisplaySetting(
    val primaryGroupSort: SortOption,
    val secondaryGroupSort: SortOption = SortOption.NONE,
    val contentSort: SortOption = SortOption.NONE,
    val showTrackNum: Boolean = false,
    val isPreset: Boolean = true,
) {
    companion object Preset {
        val TitleASC =
            DisplaySetting(
                primaryGroupSort = SortOption.Title(true),
                showTrackNum = false,
                isPreset = true,
            )
        val AlbumASC =
            DisplaySetting(
                primaryGroupSort = SortOption.Album(true),
                contentSort = SortOption.TrackNum(true),
                showTrackNum = true,
                isPreset = true,
            )
        val ArtistASC =
            DisplaySetting(
                primaryGroupSort = SortOption.Artist(true),
                showTrackNum = false,
                isPreset = true,
            )

        val ArtistAlbumASC =
            DisplaySetting(
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

fun DisplaySetting.sortOptions() =
    buildList {
        add(primaryGroupSort)
        add(secondaryGroupSort)
        add(contentSort)
    }
