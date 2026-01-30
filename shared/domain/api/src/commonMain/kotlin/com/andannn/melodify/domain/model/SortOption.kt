/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

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

    sealed interface PlayListOption : SortOption {
        data class CreateData(
            val ascending: Boolean,
        ) : PlayListOption
    }

    data object NONE : SortOption
}

/**
 * Which content to sort
 */
enum class ContentSortType {
    Audio,
    Video,
    PlayList,
}

fun ContentSortType.defaultPresetSetting() =
    when (this) {
        ContentSortType.Audio -> {
            PresetDisplaySetting.AlbumAsc
        }

        ContentSortType.Video -> {
            PresetDisplaySetting.VideoBucketNameASC
        }

        ContentSortType.PlayList -> {
            PresetDisplaySetting.PlaylistCreateDateDESC
        }
    }

fun CustomTab.contentSortType(): ContentSortType =
    when (this) {
        is CustomTab.AlbumDetail,
        is CustomTab.AllMusic,
        is CustomTab.ArtistDetail,
        is CustomTab.GenreDetail,
        -> ContentSortType.Audio

        is CustomTab.AllVideo,
        is CustomTab.BucketDetail,
        -> ContentSortType.Video

        is CustomTab.PlayListDetail -> ContentSortType.PlayList
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
        is SortOption.PlayListOption.CreateData -> ascending
        is SortOption.VideoOption.Title -> ascending
        is SortOption.NONE -> false
    }
