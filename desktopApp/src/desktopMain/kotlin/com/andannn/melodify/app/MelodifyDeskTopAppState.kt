/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.core.syncer.SyncLibraryService
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.window.MenuEvent
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

sealed interface WindowType {
    data object Home : WindowType

    data object SettingPreference : WindowType
}

@Composable
internal fun rememberMelodifyDeskTopAppState() =
    retain {
        MelodifyDeskTopAppState()
    }

internal class MelodifyDeskTopAppState : ScopedPresenter<Unit>() {
    val windowStack = mutableStateSetOf<WindowType>(WindowType.Home)

    init {
        launch {
            getKoin().get<SyncLibraryService>().startWatchingLibrary()
        }
    }

    fun handleMenuEvent(menuEvent: MenuEvent) {
        when (menuEvent) {
            MenuEvent.OnOpenMediaLibrarySettings ->
                openWindow(
                    WindowType.SettingPreference,
                )
        }
    }

    fun openWindow(windowType: WindowType) {
        windowStack.add(windowType)
    }

    fun closeWindow(
        windowType: WindowType,
        applicationScope: ApplicationScope,
    ) {
        windowStack.remove(windowType)
        if (windowStack.isEmpty()) {
            applicationScope.exitApplication()
        }
    }

    @Composable
    override fun present() {
    }
}
