/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.database.MediaSorts
import com.andannn.melodify.core.database.MediaWheres
import com.andannn.melodify.core.database.Sort
import com.andannn.melodify.core.database.SortOrder
import com.andannn.melodify.core.database.Where
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.SortOption

internal fun List<GroupKey>.toAudioWheresMethod() =
    MediaWheres
        .buildMethod {
            this@toAudioWheresMethod.forEach {
                addAudioWhereOption(it)
            }
        }.takeIf { this.isNotEmpty() }

internal fun List<GroupKey>.toVideoWheresMethod() =
    MediaWheres
        .buildMethod {
            this@toVideoWheresMethod.forEach {
                addVideoWhereOption(it)
            }
        }.takeIf { this.isNotEmpty() }

private fun MutableList<Where>.addAudioWhereOption(where: GroupKey) =
    apply {
        val where =
            when (where) {
                is GroupKey.Album -> {
                    Where(
                        "media_album_id",
                        Where.Operator.EQUALS,
                        where.albumId,
                    )
                }

                is GroupKey.Artist -> {
                    Where(
                        "media_artist_id",
                        Where.Operator.EQUALS,
                        where.artistId,
                    )
                }

                is GroupKey.Genre -> {
                    Where(
                        "media_genre_id",
                        Where.Operator.EQUALS,
                        where.genreId,
                    )
                }

                is GroupKey.Year -> {
                    Where(
                        "media_year",
                        Where.Operator.EQUALS,
                        where.year,
                    )
                }

                is GroupKey.Title -> {
                    Where(
                        "media_title",
                        Where.Operator.GLOB,
                        where.firstCharacterString + "*",
                    )
                }

                else -> {
                    error("not support")
                }
            }
        add(where)
    }

private fun MutableList<Where>.addVideoWhereOption(where: GroupKey) =
    apply {
        val where =
            when (where) {
                is GroupKey.Title -> {
                    Where(
                        "video_title",
                        Where.Operator.GLOB,
                        where.firstCharacterString + "*",
                    )
                }

                is GroupKey.BucketId -> {
                    Where(
                        "video_bucket_id",
                        Where.Operator.EQUALS,
                        where.bucketId,
                    )
                }

                else -> {
                    error("not support")
                }
            }
        add(where)
    }

internal fun List<SortOption.AudioOption>.toAudioSortMethod() =
    MediaSorts.buildMethod {
        this@toAudioSortMethod.forEach {
            addSortOption(it)
        }
    }

internal fun List<SortOption.VideoOption>.toVideoSortMethod() =
    MediaSorts.buildMethod {
        this@toVideoSortMethod.forEach {
            addSortOption(it)
        }
    }

private fun MutableList<Sort>.addSortOption(sort: SortOption) {
    when (sort) {
        is SortOption.AudioOption.Album -> {
            apply {
                add(Sort("media_album", sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.Artist -> {
            apply {
                add(Sort("media_artist", sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.Title -> {
            apply {
                add(Sort("media_title", sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.TrackNum -> {
            apply {
                add(Sort("media_cd_track_number", sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.Genre -> {
            apply {
                add(Sort("media_genre", sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.ReleaseYear -> {
            apply {
                add(Sort("media_year", sort.ascending.toOrder()))
            }
        }

        is SortOption.VideoOption.Bucket -> {
            apply {
                add(Sort("video_bucket_display_name", sort.ascending.toOrder()))
            }
        }

        is SortOption.VideoOption.Title -> {
            apply {
                add(Sort("video_title", sort.ascending.toOrder()))
            }
        }

        SortOption.NONE -> {
            apply { }
        }
    }
}

private fun Boolean.toOrder() = if (this) SortOrder.ASCENDING else SortOrder.DESCENDING
