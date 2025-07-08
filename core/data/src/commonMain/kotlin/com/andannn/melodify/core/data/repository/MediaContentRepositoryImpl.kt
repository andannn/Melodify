package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.util.mapToAlbumItemModel
import com.andannn.melodify.core.data.util.mapToArtistItemModel
import com.andannn.melodify.core.data.util.mapToAudioItemModel
import com.andannn.melodify.core.data.util.mapToGenreItemModel
import com.andannn.melodify.core.data.util.toAppItem
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.map
import kotlin.coroutines.coroutineContext

internal class MediaContentRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
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

    override suspend fun getAlbumByAlbumId(albumId: String) = mediaLibraryDao.getAlbumByAlbumId(albumId)?.toAppItem()

    override suspend fun getArtistByArtistId(artistId: String) = mediaLibraryDao.getArtistByArtistId(artistId)?.toAppItem()

    override suspend fun getGenreByGenreId(genreId: String) = mediaLibraryDao.getGenreByGenreId(genreId)?.toAppItem()

    override suspend fun searchContent(keyword: String): List<MediaItemModel> {
        val matchedAudios = mediaLibraryDao.searchMedia(keyword).map { it.toAppItem() }
        coroutineContext.ensureActive()
        val matchedAlbums = mediaLibraryDao.searchAlbum(keyword).map { it.toAppItem() }
        coroutineContext.ensureActive()
        val matchedArtists = mediaLibraryDao.searchArtist(keyword).map { it.toAppItem() }

        return matchedAudios + matchedAlbums + matchedArtists
    }
}
