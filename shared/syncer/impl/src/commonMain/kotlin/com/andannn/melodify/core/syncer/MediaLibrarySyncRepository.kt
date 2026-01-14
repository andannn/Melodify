package com.andannn.melodify.core.syncer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MediaLibrarySyncRepositoryImpl(
    private val handler: BackgroundSyncMediaStoreHandler,
) : MediaLibrarySyncRepository {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val status = MutableStateFlow<MediaLibrarySyncRepository.SyncState?>(null)

    private var syncJob: Job? = null

    override fun startSync() {
        cancelCurrentSync()
        syncJob = launchSyncJob()
    }

    override fun cancelCurrentSync() {
        status.value = null
        syncJob?.cancel()
    }

    override fun lastSyncStatusFlow(): Flow<MediaLibrarySyncRepository.SyncState> = status.filterNotNull()

    private fun launchSyncJob(): Job =
        scope.launch {
            handler.syncAllMedia().collect(::onSyncEvent)
        }

    private fun onSyncEvent(event: SyncStatusEvent) {
        status.update { current ->
            val state = current ?: MediaLibrarySyncRepository.SyncState()
            when (event) {
                is SyncStatusEvent.Progress -> {
                    val typeSyncInfo = state.syncInfoMap[event.type] ?: MediaLibrarySyncRepository.SyncInfo()

                    val newTypeSyncInfo =
                        typeSyncInfo.copy(
                            progress =
                                MediaLibrarySyncRepository.SyncInfo.Progress(
                                    event.progress,
                                    event.total,
                                ),
                        )

                    state.copy(syncInfoMap = state.syncInfoMap + (event.type to newTypeSyncInfo))
                }

                is SyncStatusEvent.Insert -> {
                    val typeSyncInfo = state.syncInfoMap[event.type] ?: MediaLibrarySyncRepository.SyncInfo()
                    state.copy(
                        syncInfoMap =
                            state.syncInfoMap + (
                                event.type to
                                    typeSyncInfo.addNewInfo(
                                        MediaLibrarySyncRepository.SyncInfo.Info(
                                            true,
                                            event.item,
                                        ),
                                    )
                            ),
                    )
                }

                is SyncStatusEvent.Delete -> {
                    val typeSyncInfo = state.syncInfoMap[event.type] ?: MediaLibrarySyncRepository.SyncInfo()
                    state.copy(
                        syncInfoMap =
                            state.syncInfoMap + (
                                event.type to
                                    typeSyncInfo.addNewInfo(
                                        MediaLibrarySyncRepository.SyncInfo.Info(
                                            false,
                                            event.item,
                                        ),
                                    )
                            ),
                    )
                }

                SyncStatusEvent.Start -> {
                    state.copy(status = MediaLibrarySyncRepository.Status.START)
                }

                SyncStatusEvent.Complete -> {
                    state.copy(status = MediaLibrarySyncRepository.Status.COMPLETED)
                }

                SyncStatusEvent.Failed -> {
                    state.copy(status = MediaLibrarySyncRepository.Status.ERROR)
                }
            }
        }
    }
}
