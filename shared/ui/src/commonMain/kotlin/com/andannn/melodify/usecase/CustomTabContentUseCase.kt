/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import androidx.paging.PagingData
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.SortOption
import kotlinx.coroutines.flow.Flow

context(repository: Repository)
fun CustomTab.contentFlow(
    sorts: List<SortOption>,
    whereGroups: List<GroupKey> = emptyList(),
): Flow<List<MediaItemModel>> =
    when (this) {
        is CustomTab.AllMusic ->
            repository.mediaContentRepository.getAllMediaItemsFlow(
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )
        is CustomTab.AllVideo ->
            repository.mediaContentRepository.getAllVideoItemsFlow(
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )

        is CustomTab.AlbumDetail ->
            repository.mediaContentRepository.getAudiosOfAlbumFlow(
                albumId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.ArtistDetail ->
            repository.mediaContentRepository.getAudiosOfArtistFlow(
                artistId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.GenreDetail ->
            repository.mediaContentRepository.getAudiosOfGenreFlow(
                genreId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.PlayListDetail ->
            repository.playListRepository.getAudiosOfPlayListFlow(
                playListId.toLong(),
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.BucketDetail ->
            repository.mediaContentRepository.getVideoBucketItemsFlow(
                bucketId,
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
    }

context(repository: Repository)
fun CustomTab.contentPagingDataFlow(
    whereGroups: List<GroupKey> = emptyList(),
    sorts: List<SortOption>,
): Flow<PagingData<out MediaItemModel>> =
    when (this) {
        is CustomTab.AllMusic ->
            repository.mediaContentRepository.getAllMediaItemsPagingFlow(
                whereGroups,
                sorts.filterIsInstance<SortOption.AudioOption>(),
            )

        is CustomTab.AlbumDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfAlbum(
                albumId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.ArtistDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfArtist(
                artistId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.GenreDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfGenre(
                genreId,
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.PlayListDetail ->
            repository.playListRepository.getAudioPagingFlowOfPlayList(
                playListId.toLong(),
                sorts.filterIsInstance<SortOption.AudioOption>(),
                whereGroups,
            )

        is CustomTab.AllVideo ->
            repository.mediaContentRepository.getAllVideoItemsPagingFlow(
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )

        is CustomTab.BucketDetail ->
            repository.mediaContentRepository.getVideoBucketItemsPagingFlow(
                bucketId,
                sorts.filterIsInstance<SortOption.VideoOption>(),
                whereGroups,
            )
    }
