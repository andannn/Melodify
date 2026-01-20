/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.datastore.UserSettingPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

internal class MediaLibrarySyncRepositoryImpl(
    private val handler: BackgroundSyncMediaStoreHandler,
    private val preferences: UserSettingPreferences,
) : MediaLibrarySyncRepository {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val status = MutableStateFlow<SyncState?>(null)

    private var syncJob: Job? = null

    override fun startSync() {
        cancelCurrentSync()
        syncJob = launchSyncJob()
    }

    override fun cancelCurrentSync() {
        status.value = null
        syncJob?.cancel()
    }

    override fun lastSyncStatusFlow() = status

    private fun launchSyncJob(): Job =
        scope.launch {
            handler.syncAllMedia().collect(::onSyncEvent)

            preferences.setLastSuccessfulSyncTime(
                Clock.System.now().toEpochMilliseconds(),
            )
        }

    private fun onSyncEvent(event: SyncStatusEvent) {
        status.update { current ->
            val state = current ?: SyncState()
            when (event) {
                is SyncStatusEvent.Progress -> {
                    val typeSyncInfo = state.syncInfoMap[event.type] ?: SyncInfo()

                    val newTypeSyncInfo =
                        typeSyncInfo.copy(
                            progress =
                                SyncInfo.Progress(
                                    event.progress,
                                    event.total,
                                ),
                        )

                    state.copy(syncInfoMap = state.syncInfoMap + (event.type to newTypeSyncInfo))
                }

                is SyncStatusEvent.Insert -> {
                    val typeSyncInfo = state.syncInfoMap[event.type] ?: SyncInfo()
                    state.copy(
                        syncInfoMap =
                            state.syncInfoMap + (
                                event.type to
                                    typeSyncInfo.addNewInfo(
                                        SyncInfo.Info(
                                            true,
                                            event.item,
                                        ),
                                    )
                            ),
                    )
                }

                is SyncStatusEvent.Delete -> {
                    val typeSyncInfo = state.syncInfoMap[event.type] ?: SyncInfo()
                    state.copy(
                        syncInfoMap =
                            state.syncInfoMap + (
                                event.type to
                                    typeSyncInfo.addNewInfo(
                                        SyncInfo.Info(
                                            false,
                                            event.item,
                                        ),
                                    )
                            ),
                    )
                }

                SyncStatusEvent.Start -> {
                    state.copy(syncStatus = SyncStatus.START)
                }

                SyncStatusEvent.Complete -> {
                    state.copy(syncStatus = SyncStatus.COMPLETED)
                }

                SyncStatusEvent.Failed -> {
                    state.copy(syncStatus = SyncStatus.ERROR)
                }
            }
        }
    }
}
