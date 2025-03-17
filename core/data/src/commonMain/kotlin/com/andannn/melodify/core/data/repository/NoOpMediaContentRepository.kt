package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class NoOpMediaContentRepository: MediaContentRepository {
    override fun getAllMediaItemsFlow(): Flow<List<AudioItemModel>> = flowOf()

    override fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>> = flowOf()

    override fun getAllArtistFlow(): Flow<List<ArtistItemModel>> = flowOf()

    override fun getAllGenreFlow(): Flow<List<GenreItemModel>> = flowOf()

    override fun getAudiosOfAlbumFlow(albumId: String): Flow<List<AudioItemModel>> = flowOf()

    override suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel> = emptyList()

    override fun getAudiosOfArtistFlow(artistId: String): Flow<List<AudioItemModel>> = flowOf()

    override suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel> = emptyList()

    override fun getAudiosOfGenreFlow(genreId: String): Flow<List<AudioItemModel>> = flowOf()

    override suspend fun getAudiosOfGenre(genreId: String): List<AudioItemModel> = emptyList()

    override fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?> = flowOf()

    override fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?> = flowOf()

    override fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?> = flowOf()

    override suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel? = null

    override suspend fun getArtistByArtistId(artistId: String): ArtistItemModel? = null

    override suspend fun getGenreByGenreId(genreId: String): GenreItemModel? = null

    override suspend fun searchContent(keyword: String): List<MediaItemModel> = emptyList()
}