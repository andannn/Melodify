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
import com.andannn.melodify.ui.core.LocalNavigationRequestEventSink
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.popup.PopupControllerImpl
import com.andannn.melodify.ui.theme.MelodifyTheme
import com.andannn.melodify.windows.librarydetail.LibraryDetailWindow
import com.andannn.melodify.windows.main.MainWindow
import com.andannn.melodify.windows.preferences.PreferenceWindow
import com.andannn.melodify.windows.search.SearchWindow
import com.andannn.melodify.windows.tabmanage.TabManageWindow

@Composable
internal fun ApplicationScope.MelodifyDeskTopApp(
    appState: MelodifyDeskTopAppState = rememberMelodifyDeskTopAppState(this),
    retainedValuesStoreRegistry: RetainedValuesStoreRegistry = retainRetainedValuesStoreRegistry(),
) {
    fun onCloseRequest(windowType: WindowType) {
        retainedValuesStoreRegistry.clearChild(windowType)
        appState.closeWindow(windowType)
    }

    val navigator: WindowNavigator = appState

    val eventSink =
        retain {
            NavigationRequestEventSink()
        }
    LaunchNavigationRequestHandlerEffect(
        eventSink = eventSink,
        navigator = navigator,
    )

    CompositionLocalProvider(
        LocalNavigationRequestEventSink provides eventSink,
    ) {
        MelodifyTheme(
            darkTheme = false,
            content = {
                appState.windowStack.forEach { windowType ->
                    retainedValuesStoreRegistry.LocalRetainedValuesStoreProvider(
                        windowType,
                    ) {
                        CompositionLocalProvider(
                            LocalPopupController provides retain { PopupControllerImpl() },
                        ) {
                            when (windowType) {
                                WindowType.Home ->
                                    MainWindow(
                                        navigator = navigator,
                                        onCloseRequest = {
                                            onCloseRequest(windowType)
                                        },
                                    )

                                WindowType.SettingPreference ->
                                    PreferenceWindow(
                                        navigator = navigator,
                                        onCloseRequest = {
                                            onCloseRequest(windowType)
                                        },
                                    )

                                is WindowType.MediaLibrary ->
                                    LibraryDetailWindow(
                                        navigator = navigator,
                                        dataSource = windowType.datasource,
                                        onCloseRequest = {
                                            onCloseRequest(windowType)
                                        },
                                    )

                                WindowType.TabManage ->
                                    TabManageWindow(
                                        navigator = navigator,
                                        onCloseRequest = {
                                            onCloseRequest(windowType)
                                        },
                                    )

                                WindowType.Search ->
                                    SearchWindow(
                                        navigator = navigator,
                                        onCloseRequest = {
                                            onCloseRequest(windowType)
                                        },
                                    )
                            }
                        }
                    }
                }
            },
        )
    }
}
