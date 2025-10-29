package com.andannn.melodify.core.syncer

import kotlinx.coroutines.flow.Flow

interface SyncMediaStoreHandler {
    fun reSyncAllMedia(): Flow<SyncStatus>
}
