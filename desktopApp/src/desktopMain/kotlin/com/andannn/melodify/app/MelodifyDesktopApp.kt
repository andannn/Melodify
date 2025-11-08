/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.RetainedValuesStoreRegistry
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.retain.retainRetainedValuesStoreRegistry
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.popup.PopupControllerImpl
import com.andannn.melodify.ui.theme.MelodifyTheme
import com.andannn.melodify.window.MenuEvent
import com.andannn.melodify.window.main.MainWindow
import com.andannn.melodify.window.preferences.PreferenceWindow

@Composable
internal fun ApplicationScope.MelodifyDeskTopApp(
    appState: MelodifyDeskTopAppState = rememberMelodifyDeskTopAppState(),
    retainedValuesStoreRegistry: RetainedValuesStoreRegistry = retainRetainedValuesStoreRegistry(),
) {
    MelodifyTheme(
        darkTheme = false,
        content = {
            appState.windowStack.forEach { windowType ->
                fun onCloseRequest() {
                    retainedValuesStoreRegistry.clearChild(windowType)
                    appState.closeWindow(windowType, this)
                }

                fun onMenuEvent(menuEvent: MenuEvent) {
                    appState.handleMenuEvent(menuEvent)
                }

                retainedValuesStoreRegistry.ProvideChildRetainedValuesStore(
                    windowType,
                ) {
                    CompositionLocalProvider(
                        LocalPopupController provides retain { PopupControllerImpl() },
                    ) {
                        when (windowType) {
                            WindowType.Home ->
                                MainWindow(
                                    onMenuEvent = ::onMenuEvent,
                                    onCloseRequest = ::onCloseRequest,
                                )

                            WindowType.SettingPreference ->
                                PreferenceWindow(
                                    onMenuEvent = ::onMenuEvent,
                                    onCloseRequest = ::onCloseRequest,
                                )
                        }
                    }
                }
            }
        },
    )
}
