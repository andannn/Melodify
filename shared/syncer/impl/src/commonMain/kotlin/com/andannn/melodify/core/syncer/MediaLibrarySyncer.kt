/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.syncer.model.FileChangeEvent
import kotlinx.coroutines.flow.Flow

internal sealed interface SyncStatusEvent {
    data class Progress(
        val type: MediaLibrarySyncRepository.ContentType,
        val progress: Int,
        val total: Int,
    ) : SyncStatusEvent

    data class Insert(
        val type: MediaLibrarySyncRepository.ContentType,
        val item: String,
    ) : SyncStatusEvent

    data class Delete(
        val type: MediaLibrarySyncRepository.ContentType,
        val item: String,
    ) : SyncStatusEvent

    data object Failed : SyncStatusEvent

    data object Start : SyncStatusEvent

    data object Complete : SyncStatusEvent
}

/**
 * Interface for syncing media library with the system media store.
 */
internal interface MediaLibrarySyncer {
    /**
     * Rescan all media data and sync the database.
     *
     * @return true if the sync was successful, false otherwise.
     */
    fun syncAllMediaLibrary(): Flow<SyncStatusEvent>

    suspend fun syncMediaByChanges(changes: List<FileChangeEvent>): Boolean
}
