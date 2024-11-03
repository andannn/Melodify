package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import kotlinx.coroutines.flow.Flow

interface MediaContentRepository {
    /**
     * Return flow of all media items
     */
    fun getAllMediaItemsFlow(): Flow<List<AudioItemModel>>

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
    fun getAudiosOfAlbumFlow(albumId: String): Flow<List<AudioItemModel>>

    /**
     * Return audios of artist
     */
    suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel>

    /**
     * Return flow of audios of artist
     */
    fun getAudiosOfArtistFlow(artistId: String): Flow<List<AudioItemModel>>

    /**
     * Return audios of artist
     */
    suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel>

    /**
     * Return flow of audios of genre
     */
    fun getAudiosOfGenreFlow(genreId: String): Flow<List<AudioItemModel>>

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
}