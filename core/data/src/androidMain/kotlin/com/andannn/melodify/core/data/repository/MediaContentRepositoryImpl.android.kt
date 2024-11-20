package com.andannn.melodify.core.data.repository

import android.provider.MediaStore
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.util.contentChangedEventFlow
import com.andannn.melodify.core.library.mediastore.MediaLibrary
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest

private const val TAG = "MediaContentRepository"

@OptIn(ExperimentalCoroutinesApi::class)
internal class MediaContentRepositoryImpl(
    // TODO: Implement with Room database source
    private val mediaLibrary: MediaLibrary
) : MediaContentRepository {

    override fun getAllMediaItemsFlow() =
        contentChangedEventFlow(allAudioUri)
            .mapLatest {
                getAllMediaItems()
            }
            .distinctUntilChanged()

    override fun getAllAlbumsFlow() =
        contentChangedEventFlow(allAlbumUri)
            .mapLatest {
                getAllAlbums()
            }
            .distinctUntilChanged()

    override fun getAllArtistFlow() =
        contentChangedEventFlow(allArtistUri)
            .mapLatest {
                getAllArtist()
            }
            .distinctUntilChanged()

    override fun getAllGenreFlow() =
        contentChangedEventFlow(allGenreUri)
            .mapLatest {
                getAllGenre()
            }
            .distinctUntilChanged()

    override fun getAudiosOfAlbumFlow(albumId: String) =
        contentChangedEventFlow(getAlbumUri(albumId.toLong()))
            .mapLatest {
                getAudiosOfAlbum(albumId)
            }
            .distinctUntilChanged()

    override fun getAudiosOfArtistFlow(artistId: String) =
        contentChangedEventFlow(getArtistUri(artistId.toLong()))
            .mapLatest {
                getAudiosOfArtist(artistId)
            }
            .distinctUntilChanged()

    override fun getAudiosOfGenreFlow(genreId: String) =
        contentChangedEventFlow(getGenreUri(genreId.toLong()))
            .mapLatest {
                getAudiosOfGenre(genreId)
            }
            .distinctUntilChanged()

    override fun getAlbumByAlbumIdFlow(albumId: String) =
        contentChangedEventFlow(getAlbumUri(albumId.toLong()))
            .mapLatest {
                getAlbumByAlbumId(albumId)
            }
            .distinctUntilChanged()

    override fun getArtistByArtistIdFlow(artistId: String) =
        contentChangedEventFlow(getArtistUri(artistId.toLong()))
            .mapLatest {
                getArtistByArtistId(artistId)
            }
            .distinctUntilChanged()

    override fun getGenreByGenreIdFlow(genreId: String) =
        contentChangedEventFlow(getGenreUri(genreId.toLong()))
            .mapLatest {
                getGenreByGenreId(genreId)
            }
            .distinctUntilChanged()

    private suspend fun getAllMediaItems() = mediaLibrary.getAllMusicData()
        .map { it.toAppItem() }

    private suspend fun getAllAlbums() = mediaLibrary.getAllAlbumData()
        .map {
            it.toAppItem() as? AlbumItemModel ?: throw IllegalStateException("Not a AlbumItem $it")
        }

    private suspend fun getAllArtist() = mediaLibrary.getAllArtistData()
        .map {
            it.toAppItem() as? ArtistItemModel
                ?: throw IllegalStateException("Not a ArtistItem $it")
        }

    private suspend fun getAllGenre() = mediaLibrary.getAllGenreData()
        .map {
            (it.toAppItem() as? GenreItemModel
                ?: throw IllegalStateException("Not a ArtistItem $it"))
        }

    override suspend fun getAudiosOfAlbum(albumId: String) =
        mediaLibrary.getAudioInAlbum(albumId.toLong())
            .map {
                it.toAppItem() as? AudioItemModel
                    ?: throw IllegalStateException("Not a audioItem $it")
            }

    override suspend fun getAudiosOfArtist(artistId: String) =
        mediaLibrary.getAudioOfArtist(artistId.toLong()).map {
            it.toAppItem() as? AudioItemModel ?: throw IllegalStateException("Not a audioItem $it")
        }

    override suspend fun getAudiosOfGenre(genreId: String) =
        mediaLibrary.getAudioOfGenre(genreId.toLong()).map {
            it.toAppItem() as? AudioItemModel ?: throw IllegalStateException("Not a audioItem $it")
        }

    override suspend fun getAlbumByAlbumId(albumId: String) =
        mediaLibrary.getAlbumById(albumId.toLong())?.let {
            it.toAppItem() as? AlbumItemModel ?: throw IllegalStateException("Invalid $it")
        }

    override suspend fun getArtistByArtistId(artistId: String) =
        mediaLibrary.getArtistById(artistId.toLong())?.let {
            it.toAppItem() as? ArtistItemModel ?: throw IllegalStateException("Invalid $it")
        }

    override suspend fun getGenreByGenreId(genreId: String): GenreItemModel? {
        Napier.d(tag = TAG, message = "getGenreByGenreId: $genreId")
        if (genreId.toLong() == -1L) {
            return GenreItemModel.UNKNOWN
        }

        return mediaLibrary.getGenreById(genreId.toLong())?.let {
            it.toAppItem() as? GenreItemModel ?: throw IllegalStateException("Invalid $it")
        }
    }

    private val allAlbumUri: String
        get() = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.toString()

    private val allArtistUri: String
        get() = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI.toString()

    private val allAudioUri: String
        get() = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()

    private val allGenreUri: String
        get() = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI.toString()

    private fun getAlbumUri(albumId: Long): String {
        return MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.toString() + "/" + albumId
    }

    private fun getArtistUri(artistId: Long): String {
        return MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI.toString() + "/" + artistId
    }

    private fun getGenreUri(genreId: Long): String {
        return MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI.toString() + "/" + genreId
    }
}
