/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.helper.paging.PagingProviderFactory
import com.andannn.melodify.domain.MediaContentRepository
import com.andannn.melodify.domain.impl.mapToAlbumItemModel
import com.andannn.melodify.domain.impl.mapToArtistItemModel
import com.andannn.melodify.domain.impl.mapToAudioItemModel
import com.andannn.melodify.domain.impl.mapToGenreItemModel
import com.andannn.melodify.domain.impl.mapToVideoItemModel
import com.andannn.melodify.domain.impl.toAppItem
import com.andannn.melodify.domain.impl.toModel
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MatchedContentTitle
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.VideoItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class MediaContentRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
) : MediaContentRepository {
    override suspend fun getAudioById(audioId: Long): AudioItemModel? =
        mediaLibraryDao.getMediaByMediaIds(listOf(audioId)).firstOrNull()?.toAppItem()

    override suspend fun getVideoById(videoId: Long): VideoItemModel? =
        mediaLibraryDao.getVideoByVideoIds(listOf(videoId)).firstOrNull()?.toAppItem()

    override fun getAllMediaItemsPagingFlow(
        whereGroup: List<GroupKey>,
        sort: List<SortOption.AudioOption>,
    ): Flow<PagingData<AudioItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                PagingProviderFactory.allMediaPagingProvider().getPagingSource(
                    whereGroup.toAudioWheresMethod(),
                    sort.toAudioSortMethod(),
                )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getAllVideoItemsPagingFlow(
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<VideoItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                PagingProviderFactory.allVideoPagingProvider().getPagingSource(
                    sort = sort.toVideoSortMethod(),
                    where = whereGroup.toVideoWheresMethod(),
                )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getVideoBucketItemsPagingFlow(
        bucketId: String,
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<VideoItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                PagingProviderFactory
                    .bucketVideoPagingProvider(
                        bucketId = bucketId,
                    ).getPagingSource(
                        sort = sort.toVideoSortMethod(),
                        where = whereGroup.toVideoWheresMethod(),
                    )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getVideoBucketItemsFlow(
        bucketId: String,
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<VideoItemModel>> =
        PagingProviderFactory
            .bucketVideoPagingProvider(
                bucketId = bucketId,
            ).getDataFlow(
                sort = sort.toVideoSortMethod(),
                where = whereGroup.toVideoWheresMethod(),
            ).map { it.mapToVideoItemModel() }

    override fun getAllMediaItemsFlow(
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ) = PagingProviderFactory
        .allMediaPagingProvider()
        .getDataFlow(
            whereGroup.toAudioWheresMethod(),
            sort.toAudioSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAllVideoItemsFlow(
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey>,
    ): Flow<List<VideoItemModel>> =
        PagingProviderFactory
            .allVideoPagingProvider()
            .getDataFlow(
                where = whereGroup.toVideoWheresMethod(),
                sort = sort.toVideoSortMethod(),
            ).map { it.mapToVideoItemModel() }

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
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ) = PagingProviderFactory
        .albumMediaPagingProvider(albumId)
        .getDataFlow(
            whereGroup.toAudioWheresMethod(),
            sort.toAudioSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAudiosPagingFlowOfAlbum(
        albumId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                PagingProviderFactory
                    .albumMediaPagingProvider(
                        albumId = albumId,
                    ).getPagingSource(
                        whereGroup.toAudioWheresMethod(),
                        sort.toAudioSortMethod(),
                    )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getAudiosOfArtistFlow(
        artistId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ) = PagingProviderFactory
        .artistMediaPagingProvider(
            artistId = artistId,
        ).getDataFlow(
            whereGroup.toAudioWheresMethod(),
            sort.toAudioSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAudiosPagingFlowOfArtist(
        artistId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ): Flow<PagingData<AudioItemModel>> =
        Pager(
            config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
            pagingSourceFactory = {
                PagingProviderFactory
                    .artistMediaPagingProvider(
                        artistId = artistId,
                    ).getPagingSource(
                        whereGroup.toAudioWheresMethod(),
                        sort.toAudioSortMethod(),
                    )
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toAppItem() }
        }

    override fun getAudiosOfGenreFlow(
        genreId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ) = PagingProviderFactory
        .genreMediaPagingProvider(
            genreId = genreId,
        ).getDataFlow(
            whereGroup.toAudioWheresMethod(),
            sort.toAudioSortMethod(),
        ).map { it.mapToAudioItemModel() }

    override fun getAudiosPagingFlowOfGenre(
        genreId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey>,
    ) = Pager(
        config = MediaPagingConfig.DEFAULT_PAGE_CONFIG,
        pagingSourceFactory = {
            PagingProviderFactory
                .genreMediaPagingProvider(
                    genreId = genreId,
                ).getPagingSource(
                    whereGroup.toAudioWheresMethod(),
                    sort.toAudioSortMethod(),
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

    override suspend fun getAlbumByAlbumId(albumId: Long) = mediaLibraryDao.getAlbumByAlbumId(albumId)?.toAppItem()

    override suspend fun getArtistByArtistId(artistId: Long) = mediaLibraryDao.getArtistByArtistId(artistId)?.toAppItem()

    override suspend fun getGenreByGenreId(genreId: Long) = mediaLibraryDao.getGenreByGenreId(genreId)?.toAppItem()

    override suspend fun getMatchedContentTitle(keyword: String): List<MatchedContentTitle> =
        mediaLibraryDao.searchContentByKeyword(keyword).map {
            it.toModel()
        }

    override suspend fun searchContent(keyword: String): List<MediaItemModel> {
        val matchedAudios = mediaLibraryDao.searchMedia(keyword).map { it.toAppItem() }
        val matchedAlbums = mediaLibraryDao.searchAlbum(keyword).map { it.toAppItem() }
        val matchedArtists = mediaLibraryDao.searchArtist(keyword).map { it.toAppItem() }

        return matchedAudios + matchedAlbums + matchedArtists
    }

    override suspend fun markMediaAsDeleted(mediaIds: List<String>) {
        mediaLibraryDao.markMediaAsDeleted(mediaIds)
    }

    override suspend fun markVideoAsDeleted(mediaIds: List<String>) {
        mediaLibraryDao.markVideoAsDeleted(mediaIds)
    }
}
