/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

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
fun CustomTab.contentFlow(): Flow<List<AudioItemModel>> =
    when (this) {
        is CustomTab.AllMusic -> repository.mediaContentRepository.getAllMediaItemsFlow()
        is CustomTab.AlbumDetail -> repository.mediaContentRepository.getAudiosOfAlbumFlow(albumId)
        is CustomTab.ArtistDetail ->
            repository.mediaContentRepository.getAudiosOfArtistFlow(
                artistId,
            )

        is CustomTab.GenreDetail -> repository.mediaContentRepository.getAudiosOfGenreFlow(genreId)
        is CustomTab.PlayListDetail ->
            repository.playListRepository.getAudiosOfPlayListFlow(
                playListId.toLong(),
            )
    }
