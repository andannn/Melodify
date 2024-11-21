package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.library.mediastore.MediaLibrary
import io.github.aakira.napier.Napier

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

internal class MediaLibrarySyncerImpl(
    private val mediaLibrary: MediaLibrary,
) : MediaLibrarySyncer {
    override suspend fun syncMediaLibrary(): Boolean {
        try {
            val mediaData = mediaLibrary.getMediaData()

        } catch (e: Exception) {
            Napier.d(tag = TAG) { "Failed to sync media library: $e" }
            return false
        }
        return false
    }
}
