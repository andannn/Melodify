/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.internal.PlayListRepository.Companion.FAVORITE_PLAY_LIST_ID
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.model.LibraryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

context(repository: Repository)
fun LibraryDataSource.content(): Flow<List<MediaItemModel>> =
    when (this) {
        LibraryDataSource.AllAlbum -> repository.mediaContentRepository.getAllAlbumsFlow()
        LibraryDataSource.AllArtist -> repository.mediaContentRepository.getAllArtistFlow()
        LibraryDataSource.AllGenre -> repository.mediaContentRepository.getAllGenreFlow()
        LibraryDataSource.AllPlaylist -> repository.playListRepository.getAllPlayListFlow()

        is LibraryDataSource.AlbumDetail ->
            repository.mediaContentRepository.getAudiosOfAlbumFlow(
                id,
                SortRule.Preset.TitleASC,
            )

        LibraryDataSource.AllSong ->
            repository.mediaContentRepository.getAllMediaItemsFlow(
                SortRule.Preset.TitleASC,
            )

        is LibraryDataSource.ArtistDetail ->
            repository.mediaContentRepository.getAudiosOfArtistFlow(
                id,
                SortRule.Preset.TitleASC,
            )

        LibraryDataSource.Favorite ->
            repository.playListRepository.getAudiosOfPlayListFlow(
                FAVORITE_PLAY_LIST_ID,
                SortRule.Preset.TitleASC,
            )

        is LibraryDataSource.GenreDetail ->
            repository.mediaContentRepository.getAudiosOfGenreFlow(
                id,
                SortRule.Preset.TitleASC,
            )

        is LibraryDataSource.PlayListDetail ->
            repository.playListRepository.getAudiosOfPlayListFlow(
                id.toLong(),
                SortRule.Preset.TitleASC,
            )
    }

context(repository: Repository)
fun LibraryDataSource.item(): Flow<MediaItemModel?> =
    when (this) {
        is LibraryDataSource.AlbumDetail -> repository.mediaContentRepository.getAlbumByAlbumIdFlow(id)
        is LibraryDataSource.ArtistDetail -> repository.mediaContentRepository.getArtistByArtistIdFlow(id)
        LibraryDataSource.Favorite -> repository.playListRepository.getPlayListFlowById(FAVORITE_PLAY_LIST_ID)
        is LibraryDataSource.GenreDetail -> repository.mediaContentRepository.getGenreByGenreIdFlow(id)
        is LibraryDataSource.PlayListDetail -> repository.getPlayListFlowById(id.toLong())
        else -> flowOf(null)
    }
