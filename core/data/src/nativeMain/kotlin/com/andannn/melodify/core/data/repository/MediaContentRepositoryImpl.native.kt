package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.dummy.mediaItems
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class MediaList(
    val media: List<MediaItem>
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

private fun MediaItem.toModel(): AudioItemModel {
    return AudioItemModel(
        id = id,
        name = title,
        artWorkUri = image,
        modifiedDate = -1,
        album = album,
        albumId = album,
        artist = artist,
        artistId = artist,
        cdTrackNumber = trackNumber ?: 0,
        discNumberIndex = 0,
        source = source,
    )
}

internal class MediaContentRepositoryImpl : MediaContentRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val mediaList: List<MediaItem> = json.decodeFromString<MediaList>(mediaItems).media

    private val albumMaps = mediaList.groupBy {
        it.album
    }

    private val artistMaps = mediaList.groupBy {
        it.artist
    }

    private val genreMaps = mediaList.groupBy {
        it.genre
    }

    override fun getAllMediaItemsFlow(): Flow<List<AudioItemModel>> {
        return flow {
            emit(
                mediaList.map { it.toModel() }
            )
        }
    }

    override fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>> {
        return flow { emit(getAllAlbums()) }
    }

    override fun getAllArtistFlow(): Flow<List<ArtistItemModel>> {
        return flow { emit(getAllArtists()) }
    }

    override fun getAllGenreFlow(): Flow<List<GenreItemModel>> {
        return flow { emit(getAllGenres()) }
    }

    override fun getAudiosOfAlbumFlow(albumId: String): Flow<List<AudioItemModel>> {
        return flow {
            emit(getAudiosOfAlbum(albumId))
        }
    }

    override suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel> {
        return albumMaps[albumId]?.map { it.toModel() } ?: emptyList()

    }

    override fun getAudiosOfArtistFlow(artistId: String): Flow<List<AudioItemModel>> {
        return flow { emit(getAudiosOfArtist(artistId)) }
    }

    override suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel> {
        return artistMaps[artistId]?.map { it.toModel() } ?: emptyList()
    }

    override fun getAudiosOfGenreFlow(genreId: String): Flow<List<AudioItemModel>> {
        return flow { emit(getAudiosOfGenre(genreId)) }
    }

    override suspend fun getAudiosOfGenre(genreId: String): List<AudioItemModel> {
        return genreMaps[genreId]?.map { it.toModel() } ?: emptyList()
    }

    override fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?> {
        return flow { emit(getAlbumByAlbumId(albumId)) }
    }

    override fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?> {
        return flow { emit(getArtistByArtistId(artistId)) }
    }

    override fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?> {
        return flow { emit(getGenreByGenreId(genreId)) }
    }

    override suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel? {
        return getAllAlbums().find { it.id == albumId }
    }

    override suspend fun getArtistByArtistId(artistId: String): ArtistItemModel? {
        return getAllArtists().find { it.id == artistId }
    }

    override suspend fun getGenreByGenreId(genreId: String): GenreItemModel? {
        return getAllGenres().find { it.id == genreId }
    }

    private fun getAllAlbums(): List<AlbumItemModel> {
        return albumMaps.keys.map {
            AlbumItemModel(
                id = it,
                name = it,
                artWorkUri = albumMaps[it]?.firstOrNull()?.image ?: "",
                trackCount = albumMaps[it]?.size ?: 0
            )
        }
    }

    private fun getAllArtists(): List<ArtistItemModel> {
        return artistMaps.keys.map {
            ArtistItemModel(
                id = it,
                name = it,
                artWorkUri = "",
                trackCount = artistMaps[it]?.size ?: 0
            )
        }
    }

    private fun getAllGenres(): List<GenreItemModel> {
        return genreMaps.keys.map {
            GenreItemModel(
                id = it,
                name = it,
                artWorkUri = "",
                trackCount = genreMaps[it]?.size ?: 0
            )
        }
    }
}