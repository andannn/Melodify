/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

data class DisplaySetting(
    val primaryGroupSort: SortOption,
    val secondaryGroupSort: SortOption = SortOption.NONE,
    val contentSort: SortOption = SortOption.NONE,
    val showTrackNum: Boolean = false,
    val isPreset: Boolean = true,
) {
    companion object Preset {
        fun getDefaultCustom(type: ContentSortType) =
            when (type) {
                ContentSortType.Audio -> Audio.DefaultCustom
                ContentSortType.Video -> Video.DefaultCustom
                ContentSortType.PlayList -> Playlist.DefaultCustom
            }

        object Video {
            val BucketNameASC =
                DisplaySetting(
                    primaryGroupSort = SortOption.VideoOption.Bucket(true),
                    contentSort = SortOption.VideoOption.Title(ascending = true),
                    showTrackNum = false,
                    isPreset = true,
                )

            val DefaultPreset = BucketNameASC
            val DefaultCustom =
                DisplaySetting(
                    primaryGroupSort = SortOption.VideoOption.Bucket(true),
                    showTrackNum = false,
                    isPreset = false,
                )
        }

        object Audio {
            val TitleASC =
                DisplaySetting(
                    primaryGroupSort = SortOption.AudioOption.Title(true),
                    showTrackNum = false,
                    isPreset = true,
                )
            val AlbumASC =
                DisplaySetting(
                    primaryGroupSort = SortOption.AudioOption.Album(true),
                    contentSort = SortOption.AudioOption.TrackNum(true),
                    showTrackNum = true,
                    isPreset = true,
                )
            val ArtistASC =
                DisplaySetting(
                    primaryGroupSort = SortOption.AudioOption.Artist(true),
                    showTrackNum = false,
                    isPreset = true,
                )

            val ArtistAlbumASC =
                DisplaySetting(
                    primaryGroupSort = SortOption.AudioOption.Artist(true),
                    secondaryGroupSort = SortOption.AudioOption.Album(true),
                    contentSort = SortOption.AudioOption.TrackNum(true),
                    showTrackNum = true,
                    isPreset = true,
                )

            val DefaultPreset = AlbumASC
            val DefaultCustom =
                DisplaySetting(
                    primaryGroupSort = SortOption.AudioOption.Album(true),
                    showTrackNum = true,
                    isPreset = false,
                )
        }

        object Playlist {
            val CreateDateDESC =
                DisplaySetting(
                    primaryGroupSort = SortOption.NONE,
                    contentSort = SortOption.PlayListOption.CreateData(false),
                    isPreset = true,
                )
            val DefaultCustom =
                DisplaySetting(
                    primaryGroupSort = SortOption.NONE,
                    contentSort = SortOption.PlayListOption.CreateData(true),
                    showTrackNum = true,
                    isPreset = false,
                )
            val DefaultPreset = CreateDateDESC
        }
    }
}

fun DisplaySetting.sortOptions() =
    buildList {
        add(primaryGroupSort)
        add(secondaryGroupSort)
        add(contentSort)
    }
