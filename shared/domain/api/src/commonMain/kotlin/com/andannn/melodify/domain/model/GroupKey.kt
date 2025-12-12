/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

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

    data class BucketId(
        val bucketId: String,
        val bucketDisplayName: String,
    ) : GroupKey
}

fun MediaItemModel.keyOf(sortOption: SortOption): GroupKey? =
    when (this) {
        is AudioItemModel -> {
            when (sortOption) {
                is SortOption.AudioOption.Album -> GroupKey.Album(albumId)
                is SortOption.AudioOption.Artist -> GroupKey.Artist(artistId)
                is SortOption.AudioOption.Genre -> GroupKey.Genre(genreId)
                is SortOption.AudioOption.ReleaseYear -> GroupKey.Year(releaseYear)
                is SortOption.AudioOption.Title -> GroupKey.Title(name[0].toString())
                is SortOption.AudioOption.TrackNum -> error("Not support")
                SortOption.NONE -> null
                else -> error("not support key")
            }
        }

        is VideoItemModel -> {
            when (sortOption) {
                is SortOption.VideoOption.Bucket -> GroupKey.BucketId(bucketId, bucketName)
                is SortOption.VideoOption.Title -> GroupKey.Title(name[0].toString())
                SortOption.NONE -> null
                else -> error("not support key $sortOption")
            }
        }

        else -> {
            error("not support key")
        }
    }
