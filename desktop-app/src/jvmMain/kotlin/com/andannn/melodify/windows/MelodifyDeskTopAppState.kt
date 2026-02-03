/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.core.syncer.SyncLibraryService
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

sealed interface WindowType {
    data object Home : WindowType

    data object SettingPreference : WindowType

    data object TabManage : WindowType

    data class MediaLibrary(
        val datasource: LibraryDataSource,
    ) : WindowType
}

private const val TAG = "MelodifyDeskTopAppState"

@Composable
internal fun rememberMelodifyDeskTopAppState(applicationScope: ApplicationScope) =
    retain(
        applicationScope,
    ) {
        MelodifyDeskTopAppState(applicationScope)
    }

internal class MelodifyDeskTopAppState(
    private val applicationScope: ApplicationScope,
) : RetainedPresenter<Unit>(),
    WindowNavigator {
    val windowStack = mutableStateSetOf<WindowType>(WindowType.Home)

    init {
        retainedScope.launch {
            getKoin().get<SyncLibraryService>().startWatchingLibrary()
        }
    }

    override fun openWindow(windowType: WindowType) {
        windowStack.add(windowType)
    }

    override fun closeWindow(windowType: WindowType) {
        Napier.d(tag = TAG) { "closeWindow: $windowType" }
        windowStack.remove(windowType)

        if (windowStack.isEmpty()) {
            applicationScope.exitApplication()
        }
    }

    @Composable
    override fun present() {
    }
}
