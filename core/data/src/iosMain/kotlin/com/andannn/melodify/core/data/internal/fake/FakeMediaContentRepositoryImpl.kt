/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal.fake

import androidx.paging.PagingData
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class MediaList(
    val media: List<MediaItem>,
)

@Serializable
private data class MediaItem(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val genre: String,
    val source: String,
    val image: String,
    val trackNumber: Int? = 0,
    val totalTrackCount: Long = 0,
    val duration: Long = 0,
    val site: String = "",
)

private fun MediaItem.toModel(): AudioItemModel =
    AudioItemModel(
        id = id,
        name = title,
        artWorkUri = image,
        modifiedDate = -1,
        album = album,
        albumId = album,
        genre = genre,
        genreId = genre,
        artist = artist,
        artistId = artist,
        cdTrackNumber = trackNumber ?: 0,
        discNumber = 0,
        releaseYear = "",
        source = source,
    )

internal class FakeMediaContentRepositoryImpl : MediaContentRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val mediaList: List<MediaItem> =
        json.decodeFromString<MediaList>(fakeDataProvider()).media

    private val albumMaps =
        mediaList.groupBy {
            it.album
        }

    private val artistMaps =
        mediaList.groupBy {
            it.artist
        }

    private val genreMaps =
        mediaList.groupBy {
            it.genre
        }

    override fun getAllMediaItemsPagingFlow(sort: DisplaySetting): Flow<PagingData<AudioItemModel>> = flowOf()

    override fun getAllMediaItemsFlow(sort: DisplaySetting): Flow<List<AudioItemModel>> =
        flow {
            emit(
                mediaList.map { it.toModel() },
            )
        }

    override fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>> = flow { emit(getAllAlbums()) }

    override fun getAllArtistFlow(): Flow<List<ArtistItemModel>> = flow { emit(getAllArtists()) }

    override fun getAllGenreFlow(): Flow<List<GenreItemModel>> = flow { emit(getAllGenres()) }

    override fun getAudiosOfAlbumFlow(
        albumId: String,
        sort: DisplaySetting,
    ): Flow<List<AudioItemModel>> =
        flow {
            emit(getAudiosOfAlbum(albumId))
        }

    override fun getAudiosPagingFlowOfAlbum(
        albumId: String,
        sort: DisplaySetting,
    ): Flow<PagingData<AudioItemModel>> = flowOf()

    override suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel> = albumMaps[albumId]?.map { it.toModel() } ?: emptyList()

    override fun getAudiosOfArtistFlow(
        artistId: String,
        sort: DisplaySetting,
    ): Flow<List<AudioItemModel>> =
        flow {
            emit(getAudiosOfArtist(artistId))
        }

    override fun getAudiosPagingFlowOfArtist(
        artistId: String,
        sort: DisplaySetting,
    ): Flow<PagingData<AudioItemModel>> = flowOf()

    override suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel> =
        artistMaps[artistId]?.map { it.toModel() } ?: emptyList()

    override fun getAudiosOfGenreFlow(
        genreId: String,
        sort: DisplaySetting,
    ): Flow<List<AudioItemModel>> = flow { emit(getAudiosOfGenre(genreId)) }

    override fun getAudiosPagingFlowOfGenre(
        genreId: String,
        sort: DisplaySetting,
    ): Flow<PagingData<AudioItemModel>> = flowOf()

    override suspend fun getAudiosOfGenre(genreId: String): List<AudioItemModel> = genreMaps[genreId]?.map { it.toModel() } ?: emptyList()

    override fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?> = flow { emit(getAlbumByAlbumId(albumId)) }

    override fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?> = flow { emit(getArtistByArtistId(artistId)) }

    override fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?> = flow { emit(getGenreByGenreId(genreId)) }

    override suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel? = getAllAlbums().find { it.id == albumId }

    override suspend fun getArtistByArtistId(artistId: String): ArtistItemModel? = getAllArtists().find { it.id == artistId }

    override suspend fun getGenreByGenreId(genreId: String): GenreItemModel? = getAllGenres().find { it.id == genreId }

    override suspend fun searchContent(keyword: String): List<MediaItemModel> = emptyList()

    private fun getAllAlbums(): List<AlbumItemModel> =
        albumMaps.keys.map {
            AlbumItemModel(
                id = it,
                name = it,
                artWorkUri = albumMaps[it]?.firstOrNull()?.image ?: "",
                trackCount = albumMaps[it]?.size ?: 0,
            )
        }

    private fun getAllArtists(): List<ArtistItemModel> =
        artistMaps.keys.map {
            ArtistItemModel(
                id = it,
                name = it,
                artWorkUri = "",
                trackCount = artistMaps[it]?.size ?: 0,
            )
        }

    private fun getAllGenres(): List<GenreItemModel> =
        genreMaps.keys.map {
            GenreItemModel(
                id = it,
                name = it,
                artWorkUri = "",
                trackCount = genreMaps[it]?.size ?: 0,
            )
        }
}
