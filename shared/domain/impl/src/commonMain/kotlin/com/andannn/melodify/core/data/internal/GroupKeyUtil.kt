/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.database.helper.paging.MediaEntitySort
import com.andannn.melodify.core.database.helper.paging.MediaEntityWhere
import com.andannn.melodify.core.database.helper.paging.MediaSorts
import com.andannn.melodify.core.database.helper.paging.MediaWheres
import com.andannn.melodify.core.database.helper.paging.Sort
import com.andannn.melodify.core.database.helper.paging.VideoEntitySort
import com.andannn.melodify.core.database.helper.paging.VideoEntityWhere
import com.andannn.melodify.core.database.helper.paging.Where
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
                    MediaEntityWhere.albumIdWhere(where.albumId)
                }

                is GroupKey.Artist -> {
                    MediaEntityWhere.artistIdWhere(where.artistId)
                }

                is GroupKey.Genre -> {
                    MediaEntityWhere.genreIdWhere(where.genreId)
                }

                is GroupKey.Year -> {
                    MediaEntityWhere.releaseYearWhere(where.year)
                }

                is GroupKey.Title -> {
                    MediaEntityWhere.titleWhere(where.firstCharacterString)
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
                    VideoEntityWhere.titleWhere(where.firstCharacterString)
                }

                is GroupKey.BucketId -> {
                    VideoEntityWhere.bucketIdWhere(where.bucketId)
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
                add(MediaEntitySort.buildAlbumSort(sort.ascending))
            }
        }

        is SortOption.AudioOption.Artist -> {
            apply {
                add(MediaEntitySort.buildArtistSort(sort.ascending))
            }
        }

        is SortOption.AudioOption.Title -> {
            apply {
                add(MediaEntitySort.buildTitleSort(sort.ascending))
            }
        }

        is SortOption.AudioOption.TrackNum -> {
            apply {
                add(MediaEntitySort.buildTrackNumSort(sort.ascending))
            }
        }

        is SortOption.AudioOption.Genre -> {
            apply {
                add(MediaEntitySort.buildGenreSort(sort.ascending))
            }
        }

        is SortOption.AudioOption.ReleaseYear -> {
            apply {
                add(MediaEntitySort.buildReleaseYearSort(sort.ascending))
            }
        }

        is SortOption.VideoOption.Bucket -> {
            apply {
                add(VideoEntitySort.buildBucketSort(sort.ascending))
            }
        }

        is SortOption.VideoOption.Title -> {
            apply {
                add(VideoEntitySort.buildTitleSort(sort.ascending))
            }
        }

        is SortOption.PlayListOption.CreateData -> {
            apply {
            }
        }

        SortOption.NONE -> {
            apply { }
        }
    }
}
