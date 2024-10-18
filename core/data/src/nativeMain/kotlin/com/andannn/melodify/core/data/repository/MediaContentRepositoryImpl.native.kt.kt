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
        albumId = album,
        artist = artist,
        artistId = artist,
        cdTrackNumber = trackNumber,
        discNumberIndex = 0,
    )
}

internal class MediaContentRepositoryImpl : MediaContentRepository {
    private var mediaList: MediaList = MediaList(emptyList())

    init {
//        try {
//            mediaList = Json.decodeFromString<MediaList>(mediaItems)
//        } catch (e: Exception) {
//            Napier.d { "JQN: exception $e" }
//        }
//        Napier.d { "JQN: mediaList $mediaList" }
    }

    override fun getAllMediaItemsFlow(): Flow<List<AudioItemModel>> {
        return flow {
//            emit(
//                mediaList.media.map { it.toModel() }
//            )
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

    override fun getAudiosOfAlbumFlow(albumId: String): Flow<List<AudioItemModel>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel> {
        return emptyList()
    }

    override fun getAudiosOfArtistFlow(artistId: String): Flow<List<AudioItemModel>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel> {
        return emptyList()
    }

    override fun getAudiosOfGenreFlow(genreId: String): Flow<List<AudioItemModel>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun getAudiosOfGenre(genreId: String): List<AudioItemModel> {
        return emptyList()
    }

    override fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?> {
        return flow { emit(null) }
    }

    override fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?> {
        return flow { emit(null) }
    }

    override fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?> {
        return flow { emit(null) }
    }

    override suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel? {
        return null
    }

    override suspend fun getArtistByArtistId(artistId: String): ArtistItemModel? {
        return null
    }

    override suspend fun getGenreByGenreId(genreId: String): GenreItemModel? {
        return null
    }
}