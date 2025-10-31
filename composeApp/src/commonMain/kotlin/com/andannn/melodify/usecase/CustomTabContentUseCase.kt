/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import androidx.paging.PagingData
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import kotlinx.coroutines.flow.Flow

context(repository: Repository)
fun CustomTab.contentFlow(sort: DisplaySetting): Flow<List<AudioItemModel>> =
    when (this) {
        is CustomTab.AllMusic -> repository.mediaContentRepository.getAllMediaItemsFlow(sort)
        is CustomTab.AlbumDetail ->
            repository.mediaContentRepository.getAudiosOfAlbumFlow(
                albumId,
                sort,
            )

        is CustomTab.ArtistDetail ->
            repository.mediaContentRepository.getAudiosOfArtistFlow(
                artistId,
                sort,
            )

        is CustomTab.GenreDetail -> repository.mediaContentRepository.getAudiosOfGenreFlow(genreId, sort)
        is CustomTab.PlayListDetail ->
            repository.playListRepository.getAudiosOfPlayListFlow(
                playListId.toLong(),
                sort,
            )
    }

context(repository: Repository)
fun CustomTab.contentPagingDataFlow(sort: DisplaySetting): Flow<PagingData<AudioItemModel>> =
    when (this) {
        is CustomTab.AllMusic -> repository.mediaContentRepository.getAllMediaItemsPagingFlow(sort)
        is CustomTab.AlbumDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfAlbum(
                albumId,
                sort,
            )

        is CustomTab.ArtistDetail ->
            repository.mediaContentRepository.getAudiosPagingFlowOfArtist(
                artistId,
                sort,
            )

        is CustomTab.GenreDetail -> repository.mediaContentRepository.getAudiosPagingFlowOfGenre(genreId, sort)
        is CustomTab.PlayListDetail ->
            repository.playListRepository.getAudioPagingFlowOfPlayList(
                playListId.toLong(),
                sort,
            )
    }
