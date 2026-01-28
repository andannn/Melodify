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
import com.andannn.melodify.domain.model.MediaItemModel
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
        bucketId: String,
        sort: List<SortOption.VideoOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<VideoItemModel>>

    fun getVideoBucketItemsFlow(
        bucketId: String,
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
        albumId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfAlbum(
        albumId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return flow of audios of artist
     */
    fun getAudiosOfArtistFlow(
        artistId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfArtist(
        artistId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return flow of audios of genre
     */
    fun getAudiosOfGenreFlow(
        genreId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfGenre(
        genreId: String,
        sort: List<SortOption.AudioOption>,
        whereGroup: List<GroupKey> = emptyList(),
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return flow of album by albumId
     */
    fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?>

    /**
     * Return flow of artist by artistId
     */
    fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?>

    /**
     * Return flow of genre by genreId
     */
    fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?>

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

    /**
     * Search content by keyword
     */
    suspend fun searchContent(keyword: String): List<MediaItemModel>

    suspend fun markMediaAsDeleted(mediaIds: List<String>)

    suspend fun markVideoAsDeleted(mediaIds: List<String>)
}
