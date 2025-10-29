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

internal class ReSyncMediaStoreHandlerHandlerImpl(
    private val context: Context,
) : SyncMediaStoreHandler {
    override fun reSyncAllMedia(): Flow<SyncStatus> =
        flow {
            val requestId = SyncWorkHelper.doOneTimeSyncWork(context)

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
