/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.ui.components.playcontrol.LocalPlayerUiController
import com.andannn.melodify.ui.components.playcontrol.PlayerUiController
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.popup.PopupControllerImpl
import com.andannn.melodify.ui.theme.MelodifyTheme
import com.andannn.melodify.window.main.MainWindow
import com.andannn.melodify.window.preferences.PreferenceWindow
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun ApplicationScope.MelodifyDeskTopApp(appState: MelodifyDeskTopAppState = rememberMelodifyDeskTopAppState()) {
    MelodifyTheme(darkTheme = false) {
        val scope = rememberCoroutineScope()
        CompositionLocalProvider(
            LocalPopupController provides remember { PopupControllerImpl() },
            LocalRepository provides remember { getKoin().get() },
            LocalPlayerUiController provides remember { PlayerUiController(scope) },
        ) {
            MainWindow(
                appState = appState,
                onCloseRequest = ::exitApplication,
            )

            if (appState.showPreferenceWindow) {
                PreferenceWindow(
                    appState = appState,
                    onCloseRequest = appState::closePreferenceWindow,
                )
            }
        }
    }
}
