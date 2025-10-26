/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.repository

import androidx.paging.PagingData
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.SortRule
import kotlinx.coroutines.flow.Flow

interface MediaContentRepository {
    fun getAllMediaItemsPagingFlow(sort: SortRule): Flow<PagingData<AudioItemModel>>

    /**
     * Return flow of all media items
     */
    fun getAllMediaItemsFlow(sort: SortRule): Flow<List<AudioItemModel>>

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
        sort: SortRule,
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfAlbum(
        albumId: String,
        sort: SortRule,
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return audios of artist
     */
    suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel>

    /**
     * Return flow of audios of artist
     */
    fun getAudiosOfArtistFlow(
        artistId: String,
        sort: SortRule,
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfArtist(
        artistId: String,
        sort: SortRule,
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return audios of artist
     */
    suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel>

    /**
     * Return flow of audios of genre
     */
    fun getAudiosOfGenreFlow(
        genreId: String,
        sort: SortRule,
    ): Flow<List<AudioItemModel>>

    fun getAudiosPagingFlowOfGenre(
        genreId: String,
        sort: SortRule,
    ): Flow<PagingData<AudioItemModel>>

    /**
     * Return audios of genre
     */
    suspend fun getAudiosOfGenre(genreId: String): List<AudioItemModel>

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
    suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel?

    /**
     * Return artist by artistId
     */
    suspend fun getArtistByArtistId(artistId: String): ArtistItemModel?

    /**
     * Return genre by genreId
     */
    suspend fun getGenreByGenreId(genreId: String): GenreItemModel?

    /**
     * Search content by keyword
     */
    suspend fun searchContent(keyword: String): List<MediaItemModel>
}
