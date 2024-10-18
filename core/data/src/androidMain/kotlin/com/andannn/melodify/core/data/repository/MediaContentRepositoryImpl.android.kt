package com.andannn.melodify.core.data.repository

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.player.MediaBrowserManager
import com.andannn.melodify.core.player.library.ALBUM_ID
import com.andannn.melodify.core.player.library.ALBUM_PREFIX
import com.andannn.melodify.core.player.library.ALL_MUSIC_ID
import com.andannn.melodify.core.player.library.ARTIST_ID
import com.andannn.melodify.core.player.library.ARTIST_PREFIX
import com.andannn.melodify.core.player.library.GENRE_ID
import com.andannn.melodify.core.player.library.GENRE_PREFIX
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.guava.await

private const val TAG = "MediaContentRepository"

@OptIn(ExperimentalCoroutinesApi::class)
internal class MediaContentRepositoryImpl(
    context: Context,
    private val mediaBrowserManager: MediaBrowserManager,
) : MediaContentRepository {
    private val mediaBrowser
        get() = mediaBrowserManager.mediaBrowser

    private val contentResolver = context.contentResolver

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

    private suspend fun getAllMediaItems() = mediaBrowser.getChildren(
        ALL_MUSIC_ID,
        0,
        Int.MAX_VALUE,
        null,
    ).await().value?.toList()
        ?.map {
            it.toAppItem() as? AudioItemModel ?: throw IllegalStateException("Not a audioItem $it")
        }
        ?: emptyList()

    private suspend fun getAllAlbums() = mediaBrowser.getChildren(
        ALBUM_ID,
        0,
        Int.MAX_VALUE,
        null,
    ).await().value?.toList()
        ?.map {
            it.toAppItem() as? AlbumItemModel ?: throw IllegalStateException("Not a AlbumItem $it")
        }
        ?: emptyList()

    private suspend fun getAllArtist() = mediaBrowser.getChildren(
        ARTIST_ID,
        0,
        Int.MAX_VALUE,
        null,
    ).await().value?.toList()
        ?.map {
            it.toAppItem() as? ArtistItemModel
                ?: throw IllegalStateException("Not a ArtistItem $it")
        }
        ?: emptyList()

    private suspend fun getAllGenre(): List<GenreItemModel> = mediaBrowser.getChildren(
        GENRE_ID,
        0,
        Int.MAX_VALUE,
        null,
    ).await().value?.toList()
        ?.map {
            (it.toAppItem() as? GenreItemModel
                ?: throw IllegalStateException("Not a ArtistItem $it"))
        }
        ?: emptyList()

    override suspend fun getAudiosOfAlbum(albumId: String) = mediaBrowser.getChildren(
        ALBUM_PREFIX + albumId,
        0,
        Int.MAX_VALUE,
        null,
    ).await().value?.toList()
        ?.map {
            it.toAppItem() as? AudioItemModel ?: throw IllegalStateException("Not a audioItem $it")
        }
        ?: emptyList()

    override suspend fun getAudiosOfArtist(artistId: String) = mediaBrowser.getChildren(
        ARTIST_PREFIX + artistId,
        0,
        Int.MAX_VALUE,
        null,
    ).await().value?.toList()
        ?.map {
            it.toAppItem() as? AudioItemModel ?: throw IllegalStateException("Not a audioItem $it")
        }
        ?: emptyList()

    override suspend fun getAudiosOfGenre(genreId: String) = mediaBrowser.getChildren(
        GENRE_PREFIX + genreId,
        0,
        Int.MAX_VALUE,
        null,
    ).await().value?.toList()
        ?.map {
            it.toAppItem() as? AudioItemModel ?: throw IllegalStateException("Not a audioItem $it")
        }
        ?: emptyList()

    override suspend fun getAlbumByAlbumId(albumId: String) = mediaBrowser.getItem(
        ALBUM_PREFIX + albumId,
    ).await().value?.let {
        it.toAppItem() as? AlbumItemModel ?: throw IllegalStateException("Invalid $it")
    }

    override suspend fun getArtistByArtistId(artistId: String) = mediaBrowser.getItem(
        ARTIST_PREFIX + artistId,
    ).await().value?.let {
        it.toAppItem() as? ArtistItemModel ?: throw IllegalStateException("Invalid $it")
    }

    override suspend fun getGenreByGenreId(genreId: String): GenreItemModel? {
        Napier.d(tag = TAG, message = "getGenreByGenreId: $genreId")
        if (genreId.toLong() == -1L) {
            return GenreItemModel.UNKNOWN
        }

        return mediaBrowser.getItem(
            GENRE_PREFIX + genreId,
        ).await().value?.let {
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

    private fun contentChangedEventFlow(uri: String): Flow<Unit> {
        Log.d(TAG, "getContentChangedEventFlow: $uri")
        return callbackFlow {
            val observer =
                object : ContentObserver(null) {
                    override fun onChange(selfChange: Boolean) {
                        trySend(Unit)
                    }
                }

            contentResolver.registerContentObserver(
                /* uri = */ Uri.parse(uri),
                /* notifyForDescendants = */ true,
                /* observer = */ observer,
            )

            trySend(Unit)

            awaitClose {
                contentResolver.unregisterContentObserver(observer)
            }
        }
    }
}