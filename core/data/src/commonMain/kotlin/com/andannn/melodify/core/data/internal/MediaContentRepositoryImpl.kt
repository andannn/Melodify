/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.SortOption
import com.andannn.melodify.core.data.model.toSortMethod
import com.andannn.melodify.core.data.model.toWheresMethod
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.coroutineContext

internal class MediaContentRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
) : MediaContentRepository {
    override fun getAllMediaItemsPagingFlow(
        whereGroup: List<GroupKey>,
        sort: List<SortOption>,
    ): Flow<PagingData<AudioItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                mediaLibraryDao.getAllMediaPagingSource(
                    whereGroup.toWheresMethod(),
                    sort.toSortMethod(),
                )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getAllMediaItemsFlow(
        sort: List<SortOption>,
        whereGroup: List<GroupKey>,
    ) = mediaLibraryDao
        .getAllMediaFlow(
            whereGroup.toWheresMethod(),
            sort.toSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAllAlbumsFlow() =
        mediaLibraryDao
            .getAllAlbumFlow()
            .map { it.mapToAlbumItemModel() }

    override fun getAllArtistFlow() =
        mediaLibraryDao
            .getAllArtistFlow()
            .map { it.mapToArtistItemModel() }

    override fun getAllGenreFlow() =
        mediaLibraryDao
            .getAllGenreFlow()
            .map { it.mapToGenreItemModel() }

    override fun getAudiosOfAlbumFlow(
        albumId: String,
        sort: List<SortOption>,
        whereGroup: List<GroupKey>,
    ) = mediaLibraryDao
        .getMediasByAlbumIdFlow(
            albumId,
            whereGroup.toWheresMethod(),
            sort.toSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAudiosPagingFlowOfAlbum(
        albumId: String,
        sort: List<SortOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                mediaLibraryDao.getMediasPagingSourceByAlbumId(
                    albumId = albumId,
                    whereGroup.toWheresMethod(),
                    sort.toSortMethod(),
                )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getAudiosOfArtistFlow(
        artistId: String,
        sort: List<SortOption>,
        whereGroup: List<GroupKey>,
    ) = mediaLibraryDao
        .getMediasByArtistIdFlow(
            artistId,
            whereGroup.toWheresMethod(),
            sort.toSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAudiosPagingFlowOfArtist(
        artistId: String,
        sort: List<SortOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                mediaLibraryDao.getMediasPagingSourceByArtistId(
                    artistId = artistId,
                    whereGroup.toWheresMethod(),
                    sort.toSortMethod(),
                )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getAudiosOfGenreFlow(
        genreId: String,
        sort: List<SortOption>,
        whereGroup: List<GroupKey>,
    ) = mediaLibraryDao
        .getMediasByGenreIdFlow(
            genreId,
            whereGroup.toWheresMethod(),
            sort.toSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAudiosPagingFlowOfGenre(
        genreId: String,
        sort: List<SortOption>,
        whereGroup: List<GroupKey>,
    ) = Pager(
        config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
        pagingSourceFactory = {
            mediaLibraryDao.getMediasPagingSourceByGenreId(
                genreId = genreId,
                whereGroup.toWheresMethod(),
                sort.toSortMethod(),
            )
        },
    ).flow.map { pagingData ->
        pagingData.map { it.toAppItem() }
    }

    override fun getAlbumByAlbumIdFlow(albumId: String) =
        mediaLibraryDao
            .getAlbumByAlbumIdFlow(albumId)
            .map { it?.toAppItem() }

    override fun getArtistByArtistIdFlow(artistId: String) =
        mediaLibraryDao
            .getArtistByArtistIdFlow(artistId)
            .map { it?.toAppItem() }

    override fun getGenreByGenreIdFlow(genreId: String) =
        mediaLibraryDao
            .getGenreByGenreIdFlow(genreId)
            .map { it?.toAppItem() }

    override suspend fun getAudiosOfAlbum(albumId: String) =
        mediaLibraryDao.getMediasByAlbumId(albumId).map {
            it.toAppItem()
        }

    override suspend fun getAudiosOfArtist(artistId: String) =
        mediaLibraryDao.getMediasByArtistId(artistId).map {
            it.toAppItem()
        }

    override suspend fun getAudiosOfGenre(genreId: String) =
        mediaLibraryDao.getMediasByGenreId(genreId).map {
            it.toAppItem()
        }

    override suspend fun getAlbumByAlbumId(albumId: String) = mediaLibraryDao.getAlbumByAlbumId(albumId)?.toAppItem()

    override suspend fun getArtistByArtistId(artistId: String) = mediaLibraryDao.getArtistByArtistId(artistId)?.toAppItem()

    override suspend fun getGenreByGenreId(genreId: String) = mediaLibraryDao.getGenreByGenreId(genreId)?.toAppItem()

    override suspend fun searchContent(keyword: String): List<MediaItemModel> {
        val matchedAudios = mediaLibraryDao.searchMedia(keyword).map { it.toAppItem() }
        coroutineContext.ensureActive()
        val matchedAlbums = mediaLibraryDao.searchAlbum(keyword).map { it.toAppItem() }
        coroutineContext.ensureActive()
        val matchedArtists = mediaLibraryDao.searchArtist(keyword).map { it.toAppItem() }

        return matchedAudios + matchedAlbums + matchedArtists
    }
}
