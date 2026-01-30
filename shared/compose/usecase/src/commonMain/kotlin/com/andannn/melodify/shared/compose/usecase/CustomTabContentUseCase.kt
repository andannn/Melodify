/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import androidx.paging.PagingData
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.Tab
import kotlinx.coroutines.flow.Flow

context(repository: Repository)
fun Tab.contentFlow(
    sorts: List<SortOption>,
    whereGroups: List<GroupKey> = emptyList(),
): Flow<List<MediaItemModel>> =
    when (this) {
        is Tab.AllMusic -> {
            repository.getAllMediaItemsFlow(
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is Tab.AllVideo -> {
            repository.getAllVideoItemsFlow(
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }

        is Tab.AlbumDetail -> {
            repository.getAudiosOfAlbumFlow(
                albumId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is Tab.ArtistDetail -> {
            repository.getAudiosOfArtistFlow(
                artistId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is Tab.GenreDetail -> {
            repository.getAudiosOfGenreFlow(
                genreId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is Tab.PlayListDetail -> {
            repository.getItemsOfPlayListFlow(
                playListId.toLong(),
                sorts.filterIsInstance<SortOption.PlayListOption>(),
                whereGroups,
            )
        }

        is Tab.BucketDetail -> {
            repository.getVideoBucketItemsFlow(
                bucketId,
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }
    }

context(repository: Repository)
fun Tab.contentPagingDataFlow(
    whereGroups: List<GroupKey> = emptyList(),
    sorts: List<SortOption>,
): Flow<PagingData<out MediaItemModel>> =
    when (this) {
        is Tab.AllMusic -> {
            repository.getAllMediaItemsPagingFlow(
                whereGroups,
                sorts.filterIsInstance<SortOption.AudioOption>(),
            )
        }

        is Tab.AlbumDetail -> {
            repository.getAudiosPagingFlowOfAlbum(
                albumId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is Tab.ArtistDetail -> {
            repository.getAudiosPagingFlowOfArtist(
                artistId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is Tab.GenreDetail -> {
            repository.getAudiosPagingFlowOfGenre(
                genreId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is Tab.PlayListDetail -> {
            repository.getItemsPagingFlowOfPlayList(
                playListId.toLong(),
                sorts.filterIsInstance<SortOption.PlayListOption>(),
                whereGroups,
            )
        }

        is Tab.AllVideo -> {
            repository.getAllVideoItemsPagingFlow(
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }

        is Tab.BucketDetail -> {
            repository.getVideoBucketItemsPagingFlow(
                bucketId,
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }
    }
