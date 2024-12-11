package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.syncer.model.FileChangeEvent
import com.andannn.melodify.core.syncer.model.FileChangeType
import io.github.aakira.napier.Napier
import kotlin.time.measureTime

private const val TAG = "MediaLibrarySyncer"

/**
 * Interface for syncing media library with the system media store.
 */
interface MediaLibrarySyncer {
    /**
     * Rescan all media data and sync the database.
     *
     * @return true if the sync was successful, false otherwise.
     */
    suspend fun syncAllMediaLibrary(): Boolean

    suspend fun syncMediaByChanges(
        changes: List<FileChangeEvent>
    ): Boolean
}

internal class MediaLibrarySyncerWrapper(
    private val mediaLibraryScanner: MediaLibraryScanner,
    private val mediaLibraryDao: MediaLibraryDao,
) : MediaLibrarySyncer {
    override suspend fun syncAllMediaLibrary(): Boolean {
        Napier.d(tag = TAG) { "Syncing media library" }

        var result: Boolean
        val consumedTime = measureTime {
            result = syncMediaLibraryInternal()
        }

        Napier.d(tag = TAG) { "Media library sync completed in success: $result, time: $consumedTime" }
        return result
    }

    override suspend fun syncMediaByChanges(changes: List<FileChangeEvent>): Boolean {
        changes
            .groupBy { it.fileChangeType }
            .forEach { (type, events) ->
                Napier.d(tag = TAG) { "Processing ${events.size} events of type $type" }
                when (type) {
                    FileChangeType.CREATE,
                    FileChangeType.MODIFY -> {
                        mediaLibraryScanner.scanMediaByUri(events.map { it.fileUri })
                    }

                    FileChangeType.DELETE -> {
                        mediaLibraryDao.deleteMediaByUri(events.map { it.fileUri })
                    }
                }
            }

        return true
    }

    private suspend fun syncMediaLibraryInternal(): Boolean {
        try {
            mediaLibraryScanner.scanAllMedia()
            return true
        } catch (e: Exception) {
            Napier.d(tag = TAG) { "Failed to sync media library: $e" }
            return false
        }
    }
}
