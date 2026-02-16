/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.syncer.model.RefreshType
import com.andannn.melodify.core.syncer.util.getDirectoryChangeFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Paths
import kotlin.io.path.isDirectory

private const val TAG = "SyncLibraryService"

internal class SyncLibraryServiceImpl(
    private val userSettingPreferences: UserSettingPreferences,
    private val syncer: MediaLibrarySyncer,
) : SyncLibraryService {
    /**
     * Start watching the library for changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun startWatchingLibrary(): Unit =
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch {
                    // Scan all media because the library may be changed when app not running.
                    Napier.d(tag = TAG) { "Start Watching Library change..." }
                    userSettingPreferences.userDate
                        .map { userSetting ->
                            userSetting.libraryPath
                        }.distinctUntilChanged()
                        .collect { _ ->
                            syncer.syncAllMediaLibrary().collect()
                        }
                }

                launch {
                    // Listen to library changes.
                    Napier.d(tag = TAG) { "Start Watching Library change..." }
                    userSettingPreferences.userDate
                        .flatMapLatest { userSetting ->
                            userSetting.libraryPath
                                .map { Paths.get(it) }
                                .filter { it.isDirectory() }
                                .let { getDirectoryChangeFlow(it) }
                        }.collect { refreshType ->
                            Napier.d(tag = TAG) { "Library change detected: $refreshType" }
                            when (refreshType) {
                                RefreshType.All -> {
                                    syncer.syncAllMediaLibrary().collect()
                                }

                                is RefreshType.ByUri -> {
                                    syncer.syncMediaByChanges(refreshType.triggerFiles)
                                }
                            }
                        }
                }
            }
        }
}
