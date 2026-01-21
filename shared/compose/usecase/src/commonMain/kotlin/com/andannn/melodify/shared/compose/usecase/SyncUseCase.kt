package com.andannn.melodify.shared.compose.usecase

import com.andannn.melodify.core.syncer.MediaLibrarySyncRepository
import com.andannn.melodify.domain.UserPreferenceRepository

context(syncer: MediaLibrarySyncRepository, repository: UserPreferenceRepository)
suspend fun startSyncMediaLibraryIfNeeded() {
    val notSynced = repository.getLastSuccessfulSyncTime() == null
    if (notSynced) {
        syncer.startSync()
    }
}
