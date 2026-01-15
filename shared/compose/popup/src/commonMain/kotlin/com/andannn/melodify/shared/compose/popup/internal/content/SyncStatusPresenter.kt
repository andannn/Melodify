/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.internal.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.syncer.MediaLibrarySyncRepository
import com.andannn.melodify.core.syncer.SyncState
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun retainSyncStatusPresenter(repository: MediaLibrarySyncRepository = getKoin().get()) =
    retainPresenter(
        repository,
    ) {
        SyncStatusPresenter(repository)
    }

private class SyncStatusPresenter(
    private val repository: MediaLibrarySyncRepository,
) : RetainedPresenter<SyncStatusDialogState>() {
    init {
        if (repository.lastSyncStatusFlow().value == null) {
            repository.startSync()
        }
    }

    @Composable
    override fun present(): SyncStatusDialogState {
        val syncStatus by repository.lastSyncStatusFlow().collectAsStateWithLifecycle()
        return SyncStatusDialogState(
            syncStatus ?: SyncState(),
        ) {
            when (it) {
                SyncStatusDialogEvent.OnCancel -> repository.cancelCurrentSync()
                SyncStatusDialogEvent.OnReSync -> repository.startSync()
            }
        }
    }
}

internal data class SyncStatusDialogState(
    val syncState: SyncState,
    val eventSink: (SyncStatusDialogEvent) -> Unit = {},
)

internal sealed interface SyncStatusDialogEvent {
    data object OnCancel : SyncStatusDialogEvent

    data object OnReSync : SyncStatusDialogEvent
}
