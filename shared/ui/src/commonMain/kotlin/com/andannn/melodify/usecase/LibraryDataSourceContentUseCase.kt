/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.sortOptions
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
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )

        LibraryDataSource.AllSong ->
            repository.mediaContentRepository.getAllMediaItemsFlow(
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )

        LibraryDataSource.AllVideo ->
            repository.mediaContentRepository.getAllVideoItemsFlow(
                DisplaySetting.Preset.Video.BucketNameASC
                    .sortOptions()
                    .filterIsInstance<SortOption.VideoOption>(),
            )

        is LibraryDataSource.ArtistDetail ->
            repository.mediaContentRepository.getAudiosOfArtistFlow(
                id,
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )

        is LibraryDataSource.GenreDetail ->
            repository.mediaContentRepository.getAudiosOfGenreFlow(
                id,
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )

        is LibraryDataSource.PlayListDetail ->
            if (isAudioPlayList) {
                repository.playListRepository.getAudiosOfPlayListFlow(
                    id.toLong(),
                    DisplaySetting.Preset.Audio.TitleASC
                        .sortOptions()
                        .filterIsInstance<SortOption.AudioOption>(),
                )
            } else {
                repository.playListRepository.getVideosOfPlayListFlow(
                    id.toLong(),
                    DisplaySetting.Preset.Video.BucketNameASC
                        .sortOptions()
                        .filterIsInstance<SortOption.VideoOption>(),
                )
            }
    }

context(repository: Repository)
fun LibraryDataSource.item(): Flow<MediaItemModel?> =
    when (this) {
        is LibraryDataSource.AlbumDetail ->
            repository.mediaContentRepository.getAlbumByAlbumIdFlow(
                id,
            )

        is LibraryDataSource.ArtistDetail ->
            repository.mediaContentRepository.getArtistByArtistIdFlow(
                id,
            )

        is LibraryDataSource.GenreDetail ->
            repository.mediaContentRepository.getGenreByGenreIdFlow(
                id,
            )

        is LibraryDataSource.PlayListDetail -> repository.getPlayListFlowById(id.toLong())
        else -> flowOf(null)
    }
