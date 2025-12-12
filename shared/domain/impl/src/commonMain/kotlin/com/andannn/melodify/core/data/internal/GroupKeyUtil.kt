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
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.VideoColumns
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.SortOption

internal fun List<GroupKey>.toWheresMethod() =
    MediaWheres
        .buildMethod {
            this@toWheresMethod.forEach {
                addWhereOption(it)
            }
        }.takeIf { this.isNotEmpty() }

internal fun MutableList<Where>.addWhereOption(where: GroupKey) =
    apply {
        val where =
            when (where) {
                is GroupKey.Album -> {
                    Where(
                        MediaColumns.ALBUM_ID,
                        Where.Operator.EQUALS,
                        where.albumId,
                    )
                }

                is GroupKey.Artist -> {
                    Where(
                        MediaColumns.ARTIST_ID,
                        Where.Operator.EQUALS,
                        where.artistId,
                    )
                }

                is GroupKey.Genre -> {
                    Where(
                        MediaColumns.GENRE_ID,
                        Where.Operator.EQUALS,
                        where.genreId,
                    )
                }

                is GroupKey.Title -> {
                    Where(
                        MediaColumns.TITLE,
                        Where.Operator.GLOB,
                        where.firstCharacterString + "*",
                    )
                }

                is GroupKey.Year -> {
                    Where(
                        MediaColumns.YEAR,
                        Where.Operator.EQUALS,
                        where.year,
                    )
                }

                is GroupKey.BucketId -> {
                    Where(
                        VideoColumns.BUCKET_ID,
                        Where.Operator.EQUALS,
                        where.bucketId,
                    )
                }
            }
        add(where)
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
        is SortOption.AudioOption.Album -> {
            apply {
                add(Sort(MediaColumns.ALBUM, sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.Artist -> {
            apply {
                add(Sort(MediaColumns.ARTIST, sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.Title -> {
            apply {
                add(Sort(MediaColumns.TITLE, sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.TrackNum -> {
            apply {
                add(Sort(MediaColumns.CD_TRACK_NUMBER, sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.Genre -> {
            apply {
                add(Sort(MediaColumns.GENRE, sort.ascending.toOrder()))
            }
        }

        is SortOption.AudioOption.ReleaseYear -> {
            apply {
                add(Sort(MediaColumns.YEAR, sort.ascending.toOrder()))
            }
        }

        is SortOption.VideoOption.Bucket -> {
            apply {
                add(Sort(VideoColumns.BUCKET_DISPLAY_NAME, sort.ascending.toOrder()))
            }
        }

        is SortOption.VideoOption.Title -> {
            apply {
                add(Sort(VideoColumns.TITLE, sort.ascending.toOrder()))
            }
        }

        SortOption.NONE -> {
            apply { }
        }
    }
}

private fun Boolean.toOrder() = if (this) SortOrder.ASCENDING else SortOrder.DESCENDING
