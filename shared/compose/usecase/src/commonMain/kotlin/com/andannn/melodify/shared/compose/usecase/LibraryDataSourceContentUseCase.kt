/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.DisplaySetting
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.sortOptions
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

context(repository: Repository)
fun LibraryDataSource.content(): Flow<List<MediaItemModel>> =
    when (this) {
        LibraryDataSource.AllAlbum -> {
            repository.getAllAlbumsFlow()
        }

        LibraryDataSource.AllArtist -> {
            repository.getAllArtistFlow()
        }

        LibraryDataSource.AllGenre -> {
            repository.getAllGenreFlow()
        }

        LibraryDataSource.AllPlaylist -> {
            repository.getAllPlayListFlow()
        }

        is LibraryDataSource.AlbumDetail -> {
            repository.getAudiosOfAlbumFlow(
                id,
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )
        }

        LibraryDataSource.AllSong -> {
            repository.getAllMediaItemsFlow(
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )
        }

        LibraryDataSource.AllVideo -> {
            repository.getAllVideoItemsFlow(
                DisplaySetting.Preset.Video.BucketNameASC
                    .sortOptions()
                    .filterIsInstance<SortOption.VideoOption>(),
            )
        }

        is LibraryDataSource.ArtistDetail -> {
            repository.getAudiosOfArtistFlow(
                id,
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )
        }

        is LibraryDataSource.GenreDetail -> {
            repository.getAudiosOfGenreFlow(
                id,
                DisplaySetting.Preset.Audio.TitleASC
                    .sortOptions()
                    .filterIsInstance<SortOption.AudioOption>(),
            )
        }

        is LibraryDataSource.PlayListDetail -> {
            if (isAudioPlayList) {
                repository.getAudiosOfPlayListFlow(
                    id.toLong(),
                    DisplaySetting.Preset.Audio.TitleASC
                        .sortOptions()
                        .filterIsInstance<SortOption.AudioOption>(),
                )
            } else {
                repository.getVideosOfPlayListFlow(
                    id.toLong(),
                    DisplaySetting.Preset.Video.BucketNameASC
                        .sortOptions()
                        .filterIsInstance<SortOption.VideoOption>(),
                )
            }
        }
    }

context(repository: Repository)
fun LibraryDataSource.item(): Flow<MediaItemModel?> =
    when (this) {
        is LibraryDataSource.AlbumDetail -> {
            repository.getAlbumByAlbumIdFlow(
                id,
            )
        }

        is LibraryDataSource.ArtistDetail -> {
            repository.getArtistByArtistIdFlow(
                id,
            )
        }

        is LibraryDataSource.GenreDetail -> {
            repository.getGenreByGenreIdFlow(
                id,
            )
        }

        is LibraryDataSource.PlayListDetail -> {
            repository.getPlayListFlowById(id.toLong())
        }

        else -> {
            flowOf(null)
        }
    }
