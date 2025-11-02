/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SyncMediaStoreHandlerImpl : SyncMediaStoreHandler {
    override fun reSyncAllMedia(): Flow<SyncStatus> = flowOf()
}
