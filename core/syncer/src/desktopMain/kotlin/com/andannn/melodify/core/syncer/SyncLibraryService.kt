package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.syncer.util.getDirectoryChangeFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import java.nio.file.Paths
import kotlin.io.path.isDirectory

private const val TAG = "SyncLibraryService"
class SyncLibraryService(
    private val userSettingPreferences: UserSettingPreferences,
    private val syncer: MediaLibrarySyncer
) {
    /**
     * Start watching the library for changes.
     */
    suspend fun startWatchingLibrary() {
        // first scan all media because the library may be changed when app not running.
        val success = syncer.syncAllMediaLibrary()
        if (!success) {
            Napier.d(tag = TAG) { "Failed to sync media library" }
            return
        }

        // second listen to library changes.
        Napier.d(tag = TAG) { "Start Watching Library change..." }
        userSettingPreferences.userDate.first().libraryPath
            .map { Paths.get(it) }
            .filter { it.isDirectory() }
            .let { getDirectoryChangeFlow(it) }
            .collect { events ->
                syncer.syncMediaByChanges(events)
            }
    }
}