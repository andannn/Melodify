package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.dummy.mediaItems
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import io.github.aakira.napier.Napier
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
    val trackNumber: Int,
    val totalTrackCount: Long,
    val duration: Long,
    val site: Long,
)

private fun MediaItem.toModel(): AudioItemModel {
    return AudioItemModel(
        id = id,
        name = title,
        artWorkUri = source,
        modifiedDate = -1,
        album = album,
        albumId = album.toLong(),
        artist = artist,
        artistId = artist.toLong(),
        cdTrackNumber = trackNumber,
        discNumberIndex = 0,
    )
}

internal class MediaContentRepositoryImpl : MediaContentRepository {
    private val mediaList = Json.decodeFromString<MediaList>(mediaItems)

    init {
        Napier.d { "mediaList: $mediaList" }
    }

    override fun getAllMediaItemsFlow(): Flow<List<AudioItemModel>> {
        return flow {
            emit(
                mediaList.media.map { it.toModel() }
            )
        }
    }

    override fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>> {
        return flow { emit(emptyList()) }
    }

    override fun getAllArtistFlow(): Flow<List<ArtistItemModel>> {
        return flow { emit(emptyList()) }
    }

    override fun getAllGenreFlow(): Flow<List<GenreItemModel>> {
        return flow { emit(emptyList()) }
    }

    override fun getAudiosOfAlbumFlow(albumId: Long): Flow<List<AudioItemModel>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getAudiosOfAlbum(albumId: Long): List<AudioItemModel> {
        return emptyList()
    }

    override fun getAudiosOfArtistFlow(artistId: Long): Flow<List<AudioItemModel>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getAudiosOfArtist(artistId: Long): List<AudioItemModel> {
        return emptyList()
    }

    override fun getAudiosOfGenreFlow(genreId: Long): Flow<List<AudioItemModel>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getAudiosOfGenre(genreId: Long): List<AudioItemModel> {
        return emptyList()
    }

    override fun getAlbumByAlbumIdFlow(albumId: Long): Flow<AlbumItemModel?> {
        return flow { emit(null) }
    }

    override fun getArtistByArtistIdFlow(artistId: Long): Flow<ArtistItemModel?> {
        return flow { emit(null) }
    }

    override fun getGenreByGenreIdFlow(genreId: Long): Flow<GenreItemModel?> {
        return flow { emit(null) }
    }

    override suspend fun getAlbumByAlbumId(albumId: Long): AlbumItemModel? {
        return null
    }

    override suspend fun getArtistByArtistId(artistId: Long): ArtistItemModel? {
        return null
    }

    override suspend fun getGenreByGenreId(genreId: Long): GenreItemModel? {
        return null
    }
}