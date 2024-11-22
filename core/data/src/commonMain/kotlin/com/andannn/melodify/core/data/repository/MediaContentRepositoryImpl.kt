package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.MediaEntity
import kotlinx.coroutines.flow.map

internal class MediaContentRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao
) : MediaContentRepository {

    override fun getAllMediaItemsFlow() =
        mediaLibraryDao.getAllMediaFlow()
            .map { it.mapToAudioItemModel() }

    override fun getAllAlbumsFlow() =
        mediaLibraryDao.getAllAlbumFlow()
            .map { it.mapToAlbumItemModel() }

    override fun getAllArtistFlow() =
        mediaLibraryDao.getAllArtistFlow()
            .map { it.mapToArtistItemModel() }

    override fun getAllGenreFlow() =
        mediaLibraryDao.getAllGenreFlow()
            .map { it.mapToGenreItemModel() }

    override fun getAudiosOfAlbumFlow(albumId: String) =
        mediaLibraryDao.getMediasByAlbumIdFlow(albumId)
            .map { it.mapToAudioItemModel() }

    override fun getAudiosOfArtistFlow(artistId: String) =
        mediaLibraryDao.getMediasByArtistIdFlow(artistId)
            .map { it.mapToAudioItemModel() }

    override fun getAudiosOfGenreFlow(genreId: String) =
        mediaLibraryDao.getMediasByGenreIdFlow(genreId)
            .map { it.mapToAudioItemModel() }

    override fun getAlbumByAlbumIdFlow(albumId: String) =
        mediaLibraryDao.getAlbumByAlbumIdFlow(albumId)
            .map { it?.toAppItem() }

    override fun getArtistByArtistIdFlow(artistId: String) =
        mediaLibraryDao.getArtistByArtistIdFlow(artistId)
            .map { it?.toAppItem() }

    override fun getGenreByGenreIdFlow(genreId: String) =
        mediaLibraryDao.getGenreByGenreIdFlow(genreId)
            .map { it?.toAppItem() }

    override suspend fun getAudiosOfAlbum(albumId: String) =
        mediaLibraryDao.getMediasByAlbumId(albumId).map {
            it.toAppItem()
        }

    override suspend fun getAudiosOfArtist(artistId: String) =
        mediaLibraryDao.getMediasByArtistId(artistId).map {
            it.toAppItem()
        }

    override suspend fun getAudiosOfGenre(genreId: String) =
        mediaLibraryDao.getMediasByGenreId(genreId).map {
            it.toAppItem()
        }

    override suspend fun getAlbumByAlbumId(albumId: String) =
        mediaLibraryDao.getAlbumByAlbumId(albumId)?.toAppItem()

    override suspend fun getArtistByArtistId(artistId: String) =
        mediaLibraryDao.getArtistByArtistId(artistId)?.toAppItem()

    override suspend fun getGenreByGenreId(genreId: String) =
        mediaLibraryDao.getGenreByGenreId(genreId)?.toAppItem()
}

private fun List<AlbumEntity>.mapToAlbumItemModel() = map {
    it.toAppItem()
}

private fun List<MediaEntity>.mapToAudioItemModel() = map {
    it.toAppItem()
}

private fun List<ArtistEntity>.mapToArtistItemModel() = map {
    it.toAppItem()
}

private fun List<GenreEntity>.mapToGenreItemModel() = map {
    it.toAppItem()
}

fun MediaEntity.toAppItem() = AudioItemModel(
    id = id.toString(),
    name = title ?: "",
    artWorkUri = cover ?: "",
    modifiedDate = modifiedDate ?: -1,
    album = album ?: "",
    albumId = albumId?.toString() ?: "",
    artist = artist ?: "",
    artistId = artistId?.toString() ?: "",
    cdTrackNumber = cdTrackNumber ?: 0,
    discNumber = discNumber ?: 0,
)

private fun AlbumEntity.toAppItem()= AlbumItemModel(
    id = albumId.toString(),
    name = title,
    artWorkUri = "",
    trackCount = trackCount ?: 0
)

private fun ArtistEntity.toAppItem() = ArtistItemModel(
    id = artistId.toString(),
    name = name,
    // TODO:
    artWorkUri = "",
    trackCount = trackCount ?: 0
)

private fun GenreEntity.toAppItem() = GenreItemModel(
    id = genreId.toString(),
    name = name ?: "",
    // TODO:
    artWorkUri = "",
    trackCount = 0
)