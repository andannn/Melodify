/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.core.LaunchNavigationRequestHandlerEffect
import com.andannn.melodify.ui.core.LocalNavigationRequestEventSink
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.core.RootNavigator
import com.andannn.melodify.ui.core.rememberPopupControllerNavEntryDecorator
import com.andannn.melodify.ui.core.rememberRetainedValueStoreNavEntryDecorator
import com.andannn.melodify.ui.routes.HomeUiScreen
import com.andannn.melodify.ui.routes.Library
import com.andannn.melodify.ui.routes.LibraryDetail
import com.andannn.melodify.ui.routes.SearchScreen
import com.andannn.melodify.ui.routes.TabManagementScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MelodifyMobileApp(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        val backStack = rememberNavBackStack(Screen.Home)
        val navigator = retain { RootNavigator() }

        DisposableEffect(backStack) {
            navigator.backStack = backStack

            onDispose {
                navigator.backStack = null
            }
        }

        val navigatorEventSink =
            retain {
                NavigationRequestEventSink()
            }

        LaunchNavigationRequestHandlerEffect(
            navigator = navigator,
            eventSink = navigatorEventSink,
        )

        CompositionLocalProvider(
            LocalNavigationRequestEventSink provides navigatorEventSink,
        ) {
            NavDisplay(
                modifier = Modifier,
                backStack = backStack,
                sceneStrategy = DialogSceneStrategy<NavKey>() then SinglePaneSceneStrategy(),
                entryDecorators =
                    listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberRetainedValueStoreNavEntryDecorator(),
                        rememberPopupControllerNavEntryDecorator(),
                    ),
                entryProvider =
                    entryProvider {
                        entry<Screen.Home> {
                            HomeUiScreen(navigator)
                        }

                        entry<Screen.TabManage> {
                            TabManagementScreen(navigator)
                        }

                        entry<Screen.Library> {
                            Library(navigator)
                        }

                        entry<Screen.LibraryDetail> { screen ->
                            LibraryDetail(
                                navigator = navigator,
                                dataSource = screen.datasource,
                            )
                        }

                        entry<Screen.Search> {
                            SearchScreen(navigator = navigator)
                        }
                    },
            )
        }
    }
}
