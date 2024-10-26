package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import kotlinx.coroutines.flow.Flow

interface MediaContentRepository {
    fun getAllMediaItemsFlow(): Flow<List<AudioItemModel>>

    fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>>

    fun getAllArtistFlow(): Flow<List<ArtistItemModel>>

    fun getAllGenreFlow(): Flow<List<GenreItemModel>>

    fun getAllPlayListFlow(): Flow<List<PlayListItemModel>>

    fun getAudiosOfAlbumFlow(albumId: String): Flow<List<AudioItemModel>>

    suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel>

    fun getAudiosOfArtistFlow(artistId: String): Flow<List<AudioItemModel>>

    suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel>

    fun getAudiosOfGenreFlow(genreId: String): Flow<List<AudioItemModel>>

    suspend fun getAudiosOfGenre(genreId: String): List<AudioItemModel>

    fun getAudiosOfPlayListFlow(playListId: Long): Flow<List<AudioItemModel>>

    suspend fun getAudiosOfPlayList(playListId: Long): List<AudioItemModel>

    fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?>

    fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?>

    fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?>

    suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel?

    suspend fun getArtistByArtistId(artistId: String): ArtistItemModel?

    suspend fun getGenreByGenreId(genreId: String): GenreItemModel?
}