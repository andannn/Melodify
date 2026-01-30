/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

data class TabSortRule(
    val primaryGroupSort: SortOption,
    val secondaryGroupSort: SortOption = SortOption.NONE,
    val contentSort: SortOption = SortOption.NONE,
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
                TabSortRule(
                    primaryGroupSort = SortOption.VideoOption.Bucket(true),
                    contentSort = SortOption.VideoOption.Title(ascending = true),
                    isPreset = true,
                )

            val DefaultPreset = BucketNameASC
            val DefaultCustom =
                TabSortRule(
                    primaryGroupSort = SortOption.VideoOption.Bucket(true),
                    isPreset = false,
                )
        }

        object Audio {
            val TitleASC =
                TabSortRule(
                    primaryGroupSort = SortOption.AudioOption.Title(true),
                    isPreset = true,
                )
            val AlbumASC =
                TabSortRule(
                    primaryGroupSort = SortOption.AudioOption.Album(true),
                    contentSort = SortOption.AudioOption.TrackNum(true),
                    isPreset = true,
                )
            val ArtistASC =
                TabSortRule(
                    primaryGroupSort = SortOption.AudioOption.Artist(true),
                    isPreset = true,
                )

            val ArtistAlbumASC =
                TabSortRule(
                    primaryGroupSort = SortOption.AudioOption.Artist(true),
                    secondaryGroupSort = SortOption.AudioOption.Album(true),
                    contentSort = SortOption.AudioOption.TrackNum(true),
                    isPreset = true,
                )

            val DefaultPreset = AlbumASC
            val DefaultCustom =
                TabSortRule(
                    primaryGroupSort = SortOption.AudioOption.Album(true),
                    isPreset = false,
                )
        }

        object Playlist {
            val CreateDateDESC =
                TabSortRule(
                    primaryGroupSort = SortOption.NONE,
                    contentSort = SortOption.PlayListOption.CreateData(false),
                    isPreset = true,
                )
            val DefaultCustom =
                TabSortRule(
                    primaryGroupSort = SortOption.NONE,
                    contentSort = SortOption.PlayListOption.CreateData(true),
                    isPreset = false,
                )
            val DefaultPreset = CreateDateDESC
        }
    }
}

fun TabSortRule.sortOptions() =
    buildList {
        add(primaryGroupSort)
        add(secondaryGroupSort)
        add(contentSort)
    }
