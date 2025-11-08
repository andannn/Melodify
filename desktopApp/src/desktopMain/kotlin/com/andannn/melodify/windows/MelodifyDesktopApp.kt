/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.retain.RetainedValuesStoreRegistry
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.retain.retainRetainedValuesStoreRegistry
import androidx.compose.ui.window.ApplicationScope
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.popup.PopupControllerImpl
import com.andannn.melodify.ui.theme.MelodifyTheme
import com.andannn.melodify.windows.librarydetail.LibraryDetailWindow
import com.andannn.melodify.windows.main.MainWindow
import com.andannn.melodify.windows.preferences.PreferenceWindow

@Composable
internal fun ApplicationScope.MelodifyDeskTopApp(
    appState: MelodifyDeskTopAppState = rememberMelodifyDeskTopAppState(this),
    retainedValuesStoreRegistry: RetainedValuesStoreRegistry = retainRetainedValuesStoreRegistry(),
) {
    MelodifyTheme(
        darkTheme = false,
        content = {
            val navigator: WindowNavigator = appState
            appState.windowStack.forEach { windowType ->
                fun onCloseRequest() {
                    retainedValuesStoreRegistry.clearChild(windowType)
                    appState.closeWindow(windowType)
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
                                    navigator = navigator,
                                    onCloseRequest = ::onCloseRequest,
                                )

                            WindowType.SettingPreference ->
                                PreferenceWindow(
                                    navigator = navigator,
                                    onCloseRequest = ::onCloseRequest,
                                )

                            is WindowType.MediaLibrary ->
                                LibraryDetailWindow(
                                    navigator = navigator,
                                    dataSource = windowType.datasource,
                                    onCloseRequest = ::onCloseRequest,
                                )
                        }
                    }
                }
            }
        },
    )
}
