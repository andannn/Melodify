package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.syncer.util.getDirectoryChangeFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.merge
import java.nio.file.Paths
import kotlin.io.path.isDirectory

internal class SyncLibraryService(
    private val userSettingPreferences: UserSettingPreferences,
    private val syncer: MediaLibrarySyncer
) {

    suspend fun startWatchingLibrary() {
        userSettingPreferences.userDate.first().libraryPath
            .filter { Paths.get(it).isDirectory() }
            .map { getDirectoryChangeFlow(it) }
            .merge()
            .collect { events ->
                syncer.syncMediaByChanges(events)
            }
    }
}