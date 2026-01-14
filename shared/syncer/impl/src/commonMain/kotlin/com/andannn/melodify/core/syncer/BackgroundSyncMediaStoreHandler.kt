/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import kotlinx.coroutines.flow.Flow

internal interface BackgroundSyncMediaStoreHandler {
    fun syncAllMedia(): Flow<SyncStatusEvent>
}

internal class DefaultBackgroundSyncMediaStoreHandler(
    private val syncer: MediaLibrarySyncer,
) : BackgroundSyncMediaStoreHandler {
    override fun syncAllMedia(): Flow<SyncStatusEvent> = syncer.syncAllMediaLibrary()
}
