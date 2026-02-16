/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain

import androidx.paging.PagingData
import com.andannn.melodify.domain.model.AlbumItemModel
import com.andannn.melodify.domain.model.ArtistItemModel
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GenreItemModel
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MatchedContentTitle
import com.andannn.melodify.domain.model.SortOption
import com.andannn.melodify.domain.model.VideoItemModel
import kotlinx.coroutines.flow.Flow

interface MediaContentRepository {
    suspend fun getAudioById(audioId: Long): AudioItemModel?

    suspend fun getVideoById(videoId: Long): VideoItemModel?

    fun getAllMediaItemsPagingFlow(
        whereGroup: List<GroupKey> = emptyList(),
        sort: List<SortOption.AudioOption>,
    ): Flow<PagingData<AudioItemModel>>

    fun getAllVideoItemsPagingFlow(
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<VideoItemModel>>

    fun getVideoBucketItemsPagingFlow(
        bucketId: Long,
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<VideoItemModel>>

    fun getVideoBucketItemsFlow(
        bucketId: Long,
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<VideoItemModel>>

    /**
     * Return flow of all media items
     */
    fun getAllMediaItemsFlow(
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    /**
     * Return flow of all video items
     */
    fun getAllVideoItemsFlow(
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<VideoItemModel>>

    /**
     * Return flow of all albums
     */
    fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>>

    /**
     * Return flow of all artists
     */
    fun getAllArtistFlow(): Flow<List<ArtistItemModel>>

    /**
     * Return flow of all genres
     */
    fun getAllGenreFlow(): Flow<List<GenreItemModel>>

    /**
     * Return flow of audios of album
     */
    fun getAudiosOfAlbumFlow(
        albumId: Long,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfAlbum(
        albumId: Long,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return flow of audios of artist
     */
    fun getAudiosOfArtistFlow(
        artistId: Long,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfArtist(
        artistId: Long,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return flow of audios of genre
     */
    fun getAudiosOfGenreFlow(
        genreId: Long,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfGenre(
        genreId: Long,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return flow of album by albumId
     */
    fun getAlbumByAlbumIdFlow(albumId: Long): Flow<AlbumItemModel?>

    /**
     * Return flow of artist by artistId
     */
    fun getArtistByArtistIdFlow(artistId: Long): Flow<ArtistItemModel?>

    /**
     * Return flow of genre by genreId
     */
    fun getGenreByGenreIdFlow(genreId: Long): Flow<GenreItemModel?>

    /**
     * Return album by albumId
     */
    suspend fun getAlbumByAlbumId(albumId: Long): AlbumItemModel?

    /**
     * Return artist by artistId
     */
    suspend fun getArtistByArtistId(artistId: Long): ArtistItemModel?

    /**
     * Return genre by genreId
     */
    suspend fun getGenreByGenreId(genreId: Long): GenreItemModel?

    /**
     * Search content by keyword
     */
    suspend fun getMatchedContentTitle(keyword: String): List<MatchedContentTitle>

    suspend fun markMediaAsDeleted(mediaIds: List<Long>)

    suspend fun markVideoAsDeleted(mediaIds: List<Long>)
}
