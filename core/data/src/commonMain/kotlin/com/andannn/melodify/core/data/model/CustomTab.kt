/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

import androidx.paging.PagingData
import com.andannn.melodify.core.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
sealed interface CustomTab {
    @Serializable
    data object AllMusic : CustomTab

    @Serializable
    data class AlbumDetail(
        val albumId: String,
        val label: String,
    ) : CustomTab

    @Serializable
    data class ArtistDetail(
        val artistId: String,
        val label: String,
    ) : CustomTab

    @Serializable
    data class GenreDetail(
        val genreId: String,
        val label: String,
    ) : CustomTab

    @Serializable
    data class PlayListDetail(
        val playListId: String,
        val label: String,
    ) : CustomTab
}

context(repository: Repository)
fun CustomTab.contentFlow(sort: SortRule): Flow<List<AudioItemModel>> =
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
fun CustomTab.contentPagingDataFlow(sort: SortRule): Flow<PagingData<AudioItemModel>> =
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
