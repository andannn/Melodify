/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.MediaType
import com.andannn.melodify.core.syncer.model.FileChangeEvent
import com.andannn.melodify.core.syncer.model.FileChangeType
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

private const val TAG = "MediaLibrarySyncer"

internal class MediaLibrarySyncerWrapper(
    private val mediaLibraryScanner: MediaLibraryScanner,
    private val mediaLibraryDao: MediaLibraryDao,
) : MediaLibrarySyncer {
    override suspend fun syncAllMediaLibrary(): Flow<SyncStatus> = syncMediaLibraryInternal()

    override suspend fun syncMediaByChanges(changes: List<FileChangeEvent>): Boolean {
        changes
            .groupBy { it.fileChangeType }
            .forEach { (type, events) ->
                Napier.d(tag = TAG) { "Processing ${events.size} events of type $type" }
                when (type) {
                    FileChangeType.MODIFY -> {
                        val mediaData =
                            mediaLibraryScanner.scanMediaByUri(events.map { it.fileUri })

                        mediaLibraryDao.upsertMedia(
                            mediaData.albumData.toAlbumEntity(),
                            mediaData.artistData.toArtistEntity(),
                            mediaData.genreData.toGenreEntity(),
                            mediaData.audioData.toMediaEntity(),
                            mediaData.videoData.toVideoEntity(),
                        )
                    }

                    FileChangeType.DELETE -> {
                        Napier.d(tag = TAG) { "Processing Delete event: ${events.map { it.fileUri }}" }
                        mediaLibraryDao.deleteMediaByUris(events.map { it.fileUri })
                    }
                }
            }

        return true
    }

    private fun syncMediaLibraryInternal(): Flow<SyncStatus> =
        channelFlow {
            try {
                val mediaData = mediaLibraryScanner.scanAllMedia()
                trySend(SyncStatus.Start)

                mediaLibraryDao.syncMediaLibrary(
                    albums = mediaData.albumData.toAlbumEntity(),
                    artists = mediaData.artistData.toArtistEntity(),
                    genres = mediaData.genreData.toGenreEntity(),
                    audios = mediaData.audioData.toMediaEntity(),
                    videos = mediaData.videoData.toVideoEntity(),
                ) { type, inserted, total ->
                    Napier.d(tag = TAG) { "Media sync process $type: inserted: $inserted,  total: $total" }
                    trySend(SyncStatus.Progress(type.toSyncType(), inserted, total))
                }
                trySend(SyncStatus.Complete)
            } catch (e: Exception) {
                Napier.d(tag = TAG) { "Failed to sync media library: $e" }
                trySend(SyncStatus.Failed)
            } finally {
                close()
            }
        }
}

private fun Int.toSyncType() =
    when (this) {
        MediaType.ALBUM -> SyncType.ALBUM
        MediaType.ARTIST -> SyncType.ARTIST
        MediaType.GENRE -> SyncType.GENRE
        MediaType.MEDIA -> SyncType.MEDIA
        MediaType.VIDEO -> SyncType.VIDEO
        else -> error("never")
    }
