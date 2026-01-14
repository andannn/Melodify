/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.syncer.model.FileChangeEvent
import kotlinx.coroutines.flow.Flow

enum class SyncType {
    MEDIA,
    ARTIST,
    ALBUM,
    GENRE,
    VIDEO,
}

sealed interface SyncStatus {
    data class Progress(
        val type: SyncType,
        val progress: Int,
        val total: Int,
    ) : SyncStatus

    data class Insert(
        val type: SyncType,
        val items: List<String>,
    ) : SyncStatus

    data class Delete(
        val type: SyncType,
        val items: List<String>,
    ) : SyncStatus

    data object Failed : SyncStatus

    data object Start : SyncStatus

    data object Complete : SyncStatus
}

/**
 * Interface for syncing media library with the system media store.
 */
interface MediaLibrarySyncer {
    /**
     * Rescan all media data and sync the database.
     *
     * @return true if the sync was successful, false otherwise.
     */
    fun syncAllMediaLibrary(): Flow<SyncStatus>

    suspend fun syncMediaByChanges(changes: List<FileChangeEvent>): Boolean
}
