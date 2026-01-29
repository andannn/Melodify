/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import androidx.paging.PagingData
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

context(repository: Repository)
fun CustomTab.contentFlow(
    sorts: List<SortOption>,
    whereGroups: List<GroupKey> = emptyList(),
): Flow<List<MediaItemModel>> =
    when (this) {
        is CustomTab.AllMusic -> {
            repository.getAllMediaItemsFlow(
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is CustomTab.AllVideo -> {
            repository.getAllVideoItemsFlow(
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }

        is CustomTab.AlbumDetail -> {
            repository.getAudiosOfAlbumFlow(
                albumId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is CustomTab.ArtistDetail -> {
            repository.getAudiosOfArtistFlow(
                artistId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is CustomTab.GenreDetail -> {
            repository.getAudiosOfGenreFlow(
                genreId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is CustomTab.PlayListDetail -> {
            repository.getItemsOfPlayListFlow(
                playListId.toLong(),
                sorts.filterIsInstance<SortOption.PlayListOption>(),
                whereGroups,
            )
        }

        is CustomTab.BucketDetail -> {
            repository.getVideoBucketItemsFlow(
                bucketId,
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }
    }

context(repository: Repository)
fun CustomTab.contentPagingDataFlow(
    whereGroups: List<GroupKey> = emptyList(),
    sorts: List<SortOption>,
): Flow<PagingData<out MediaItemModel>> =
    when (this) {
        is CustomTab.AllMusic -> {
            repository.getAllMediaItemsPagingFlow(
                whereGroups,
                sorts.filterIsInstance<SortOption.AudioOption>(),
            )
        }

        is CustomTab.AlbumDetail -> {
            repository.getAudiosPagingFlowOfAlbum(
                albumId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is CustomTab.ArtistDetail -> {
            repository.getAudiosPagingFlowOfArtist(
                artistId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is CustomTab.GenreDetail -> {
            repository.getAudiosPagingFlowOfGenre(
                genreId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        }

        is CustomTab.PlayListDetail -> {
            repository.getItemsPagingFlowOfPlayList(
                playListId.toLong(),
                sorts.filterIsInstance<SortOption.PlayListOption>(),
                whereGroups,
            )
        }

        is CustomTab.AllVideo -> {
            repository.getAllVideoItemsPagingFlow(
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }

        is CustomTab.BucketDetail -> {
            repository.getVideoBucketItemsPagingFlow(
                bucketId,
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
        }
    }
