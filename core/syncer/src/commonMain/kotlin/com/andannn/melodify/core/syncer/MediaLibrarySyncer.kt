package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.dao.MediaLibraryDao
import io.github.aakira.napier.Napier
import kotlin.time.measureTime

private const val TAG = "MediaLibrarySyncer"

/**
 * Interface for syncing media library with the system media store.
 */
interface MediaLibrarySyncer {
    /**
     * Sync media library with the system media store.
     *
     * @return true if the sync was successful, false otherwise.
     */
    suspend fun syncMediaLibrary(): Boolean
}

internal class MediaLibrarySyncerWrapper(
    private val mediaLibraryScanner: MediaLibraryScanner,
) : MediaLibrarySyncer {
    override suspend fun syncMediaLibrary(): Boolean {
        Napier.d(tag = TAG) { "Syncing media library" }

        var result: Boolean
        val consumedTime = measureTime {
            result = syncMediaLibraryInternal()
        }

        Napier.d(tag = TAG) { "Media library sync completed in success: $result, time: $consumedTime" }
        return result
    }

    private suspend fun syncMediaLibraryInternal(): Boolean {
        try {
            mediaLibraryScanner.scanMediaDataAndSyncDatabase()
            return true
        } catch (e: Exception) {
            Napier.d(tag = TAG) { "Failed to sync media library: $e" }
            return false
        }
    }
}
