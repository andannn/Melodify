/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

import androidx.paging.PagingData
import com.andannn.melodify.core.data.Repository
import kotlinx.coroutines.flow.Flow

sealed class CustomTab(
    open val tabId: Long,
) {
    data class AllMusic(
        override val tabId: Long,
    ) : CustomTab(tabId)

    data class AlbumDetail(
        override val tabId: Long,
        val albumId: String,
        val label: String,
    ) : CustomTab(tabId)

    data class ArtistDetail(
        override val tabId: Long,
        val artistId: String,
        val label: String,
    ) : CustomTab(tabId)

    data class GenreDetail(
        override val tabId: Long,
        val genreId: String,
        val label: String,
    ) : CustomTab(tabId)

    data class PlayListDetail(
        override val tabId: Long,
        val playListId: String,
        val label: String,
    ) : CustomTab(tabId)
}

enum class TabKind {
    ALBUM,
    ARTIST,
    GENRE,
    PLAYLIST,
    ALL_MUSIC,
}

fun CustomTab.toTabKind(): TabKind =
    when (this) {
        is CustomTab.AlbumDetail -> TabKind.ALBUM
        is CustomTab.ArtistDetail -> TabKind.ARTIST
        is CustomTab.GenreDetail -> TabKind.GENRE
        is CustomTab.PlayListDetail -> TabKind.PLAYLIST
        is CustomTab.AllMusic -> TabKind.ALL_MUSIC
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
