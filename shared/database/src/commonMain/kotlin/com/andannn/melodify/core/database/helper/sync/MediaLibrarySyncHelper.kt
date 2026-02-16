/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.sync

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.andannn.melodify.core.database.MediaType
import com.andannn.melodify.core.database.MelodifyDataBase
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.internal.SyncerDao
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.AudioEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.VideoEntity

private const val DEFAULT_CHUNK_SIZE = 500

class MediaLibrarySyncHelper internal constructor(
    private val db: MelodifyDataBase,
    private val dao: MediaLibraryDao,
    private val syncerDao: SyncerDao,
) {
    suspend fun upsertMedia(
        albums: List<AlbumEntity>,
        artists: List<ArtistEntity>,
        genres: List<GenreEntity>,
        audios: List<AudioEntity>,
        videos: List<VideoEntity> = emptyList(),
    ) {
        db.useWriterConnection {
            it.immediateTransaction {
                syncerDao.upsertAlbums(albums)
                syncerDao.upsertArtist(artists)
                syncerDao.upsertGenres(genres)
                syncerDao.upsertMedias(audios)
                syncerDao.upsertVideos(videos)
            }
        }
    }

    suspend fun syncMediaLibrary(
        albums: List<AlbumEntity> = emptyList(),
        artists: List<ArtistEntity> = emptyList(),
        genres: List<GenreEntity> = emptyList(),
        audios: List<AudioEntity> = emptyList(),
        videos: List<VideoEntity> = emptyList(),
        onInsert: (type: Int, items: List<String>) -> Unit = { _, _ -> },
        onDelete: (type: Int, items: List<String>) -> Unit = { _, _ -> },
        onProgress: (type: Int, inserted: Int, total: Int) -> Unit = { _, _, _ -> },
    ) {
        db.useWriterConnection {
            it.immediateTransaction {
                syncAlbum(
                    albums,
                    onProgress = { inserted, total ->
                        onProgress(MediaType.ALBUM, inserted, total)
                    },
                    onInsert = { onInsert(MediaType.ALBUM, it) },
                    onDelete = { onDelete(MediaType.ALBUM, it) },
                )
                syncArtist(
                    artists,
                    onProgress = { inserted, total ->
                        onProgress(MediaType.ARTIST, inserted, total)
                    },
                    onInsert = { onInsert(MediaType.ARTIST, it) },
                    onDelete = { onDelete(MediaType.ARTIST, it) },
                )
                syncGenre(
                    genres,
                    onProgress = { inserted, total ->
                        onProgress(MediaType.GENRE, inserted, total)
                    },
                )
                syncMedia(
                    audios,
                    onProgress = { inserted, total ->
                        onProgress(MediaType.MEDIA, inserted, total)
                    },
                    onInsert = { onInsert(MediaType.MEDIA, it) },
                    onDelete = { onDelete(MediaType.MEDIA, it) },
                )
                syncVideo(
                    videos,
                    onProgress = { inserted, total ->
                        onProgress(MediaType.VIDEO, inserted, total)
                    },
                    onInsert = { onInsert(MediaType.VIDEO, it) },
                    onDelete = { onDelete(MediaType.VIDEO, it) },
                )

                dao.deleteOrphanAlbums()
                dao.deleteOrphanGenres()
                dao.deleteOrphanArtists()
            }
        }
    }

    private suspend fun syncMedia(
        audios: List<AudioEntity>,
        onProgress: (inserted: Int, total: Int) -> Unit,
        onInsert: (items: List<String>) -> Unit = {},
        onDelete: (items: List<String>) -> Unit = {},
    ) {
        syncTable(
            newItems = audios,
            idSelector = { it.id },
            fetchLocalIdsDao = { syncerDao.getAllMediaID() },
            deleteDao = { syncerDao.deleteMediasByIds(it) },
            upsertDao = { syncerDao.upsertMedias(it) },
            onProgress = onProgress,
            onNewInsert = { onInsert(syncerDao.getNameOfMedia(it)) },
            onBeforeDelete = { onDelete(syncerDao.getNameOfMedia(it)) },
        )
    }

    private suspend fun syncVideo(
        audios: List<VideoEntity>,
        onProgress: (inserted: Int, total: Int) -> Unit,
        onInsert: (items: List<String>) -> Unit = {},
        onDelete: (items: List<String>) -> Unit = {},
    ) {
        syncTable(
            newItems = audios,
            idSelector = { it.id },
            fetchLocalIdsDao = { syncerDao.getAllVideoID() },
            deleteDao = { syncerDao.deleteVideoByIds(it) },
            upsertDao = { syncerDao.upsertVideos(it) },
            onProgress = onProgress,
            onNewInsert = { onInsert(syncerDao.getNameOfVideo(it)) },
            onBeforeDelete = { onDelete(syncerDao.getNameOfVideo(it)) },
        )
    }

    private suspend fun syncAlbum(
        albums: List<AlbumEntity>,
        onProgress: (inserted: Int, total: Int) -> Unit,
        onInsert: (items: List<String>) -> Unit = {},
        onDelete: (items: List<String>) -> Unit = {},
    ) {
        syncTable(
            newItems = albums,
            idSelector = { it.albumId },
            fetchLocalIdsDao = { dao.getAllAlbumID() },
            deleteDao = { syncerDao.deleteAlbumsByIds(it) },
            upsertDao = { syncerDao.upsertAlbums(it) },
            onProgress = onProgress,
            onNewInsert = { onInsert(syncerDao.getNameOfAlbum(it)) },
            onBeforeDelete = { onDelete(syncerDao.getNameOfAlbum(it)) },
        )
    }

    private suspend fun syncArtist(
        artists: List<ArtistEntity>,
        onProgress: (inserted: Int, total: Int) -> Unit,
        onInsert: (items: List<String>) -> Unit = {},
        onDelete: (items: List<String>) -> Unit = {},
    ) {
        syncTable(
            newItems = artists,
            idSelector = { it.artistId },
            fetchLocalIdsDao = { syncerDao.getAllArtistID() },
            deleteDao = { syncerDao.deleteArtistsByIds(it) },
            upsertDao = { syncerDao.upsertArtist(it) },
            onProgress = onProgress,
            onNewInsert = { onInsert(syncerDao.getNameOfArtist(it)) },
            onBeforeDelete = { onDelete(syncerDao.getNameOfArtist(it)) },
        )
    }

    private suspend fun syncGenre(
        artists: List<GenreEntity>,
        onProgress: (inserted: Int, total: Int) -> Unit,
    ) {
        syncTable(
            newItems = artists,
            idSelector = { it.genreId },
            fetchLocalIdsDao = { syncerDao.getAllGenreID() },
            deleteDao = { syncerDao.deleteGenreByIds(it.filterNotNull()) },
            upsertDao = { syncerDao.upsertGenres(it) },
            onProgress = onProgress,
        )
    }

    private inline fun <T, K> syncTable(
        newItems: List<T>,
        idSelector: (T) -> K,
        fetchLocalIdsDao: () -> List<K>,
        deleteDao: (List<K>) -> Unit,
        upsertDao: (List<T>) -> List<K>,
        onBeforeDelete: (ids: List<K>) -> Unit = {},
        onNewInsert: (ids: List<K>) -> Unit = {},
        onProgress: (inserted: Int, total: Int) -> Unit,
        chunkSize: Int = DEFAULT_CHUNK_SIZE,
    ) {
        val localIds = fetchLocalIdsDao().toHashSet()

        val newIds = newItems.map(idSelector).toHashSet()

        val idsToDelete = localIds - newIds

        if (idsToDelete.isNotEmpty()) {
            idsToDelete.chunked(chunkSize).forEach { batch ->
                onBeforeDelete(batch)
                deleteDao(batch.toList())
            }
        }

        var currentCount = 0
        if (newItems.isNotEmpty()) {
            onProgress(currentCount, newItems.size)
            newItems.chunked(chunkSize).forEach { batch ->
                val ids = upsertDao(batch)

                val newIds = ids.filter { it != -1 }
                if (newIds.isNotEmpty()) {
                    onNewInsert(newIds)
                }

                currentCount += batch.size
                onProgress(currentCount, newItems.size)
            }
        }
    }
}
