/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import android.content.Context
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

internal class BackgroundSyncMediaStoreHandlerHandlerImpl(
    private val context: Context,
    private val syncWorkHelper: SyncWorkHelper,
) : BackgroundSyncMediaStoreHandler {
    override fun syncAllMedia(): Flow<SyncStatusEvent> =
        flow {
            val requestId = syncWorkHelper.doOneTimeSyncWork(context)

            val statusFlow =
                WorkManager
                    .getInstance(context)
                    .getWorkInfoByIdFlow(requestId)
                    .filterNotNull()
                    .mapNotNull { info ->
                        info.progress.toSyncStatus()
                    }

            emitAll(statusFlow)
        }
}
