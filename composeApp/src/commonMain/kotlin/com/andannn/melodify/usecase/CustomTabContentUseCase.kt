/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import androidx.paging.PagingData
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.SortOption
import kotlinx.coroutines.flow.Flow

context(repository: Repository)
fun CustomTab.contentFlow(
    sorts: List<SortOption>,
    whereGroups: List<GroupKey> = emptyList(),
): Flow<List<AudioItemModel>> =
    when (this) {
        is CustomTab.AllMusic ->
            repository.mediaContentRepository.getAllMediaItemsFlow(
                sorts,
                whereGroups,
            )

        is CustomTab.AlbumDetail ->
            repository.mediaContentRepository.getAudiosOfAlbumFlow(
                albumId,
                sorts,
                whereGroups,
            )

        is CustomTab.ArtistDetail ->
            repository.mediaContentRepository.getAudiosOfArtistFlow(
                artistId,
                sorts,
                whereGroups,
            )

        is CustomTab.GenreDetail ->
            repository.mediaContentRepository.getAudiosOfGenreFlow(
                genreId,
                sorts,
                whereGroups,
            )

        is CustomTab.PlayListDetail ->
            repository.playListRepository.getAudiosOfPlayListFlow(
                playListId.toLong(),
                sorts,
                whereGroups,
            )
    }

context(repository: Repository)
fun CustomTab.contentPagingDataFlow(
    whereGroups: List<GroupKey> = emptyList(),
    sorts: List<SortOption>,
): Flow<PagingData<AudioItemModel>> =
    when (this) {
        is CustomTab.AllMusic ->
            repository.mediaContentRepository.getAllMediaItemsPagingFlow(
                whereGroups,
                sorts,
            )

        is CustomTab.AlbumDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfAlbum(
                albumId,
                sorts,
                whereGroups,
            )

        is CustomTab.ArtistDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfArtist(
                artistId,
                sorts,
                whereGroups,
            )

        is CustomTab.GenreDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfGenre(
                genreId,
                sorts,
                whereGroups,
            )

        is CustomTab.PlayListDetail ->
            repository.playListRepository.getAudioPagingFlowOfPlayList(
                playListId.toLong(),
                sorts,
                whereGroups,
            )
    }
