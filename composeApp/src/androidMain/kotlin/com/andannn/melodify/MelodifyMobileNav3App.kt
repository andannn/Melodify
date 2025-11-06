/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.routes.HomeUiScreen
import com.andannn.melodify.ui.routes.Library
import com.andannn.melodify.ui.routes.LibraryDetail
import com.andannn.melodify.ui.routes.SearchScreen
import com.andannn.melodify.ui.routes.TabManagementScreen
import com.andannn.melodify.ui.routes.rememberHomeUiPresenter
import com.andannn.melodify.ui.routes.rememberLibraryDetailScreenPresenter
import com.andannn.melodify.ui.routes.rememberLibraryPresenter
import com.andannn.melodify.ui.routes.rememberSearchScreenPresenter
import com.andannn.melodify.ui.routes.rememberTabManagementScreenPresenter
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MelodifyMobileNav3App(modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalRepository provides remember { getKoin().get() }) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background,
        ) {
            val backStack =
                rememberNavBackStack(
                    Screen.Home,
                )
            val navigator = remember(backStack) { RootNavigator(backStack) }

            CompositionLocalProvider {
                NavDisplay(
                    modifier = Modifier,
                    backStack = navigator.backStackList,
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
                                Library(
                                    rememberLibraryPresenter(navigator).present(),
                                )
                            }

                            entry<Screen.LibraryDetail> { screen ->
                                LibraryDetail(
                                    rememberLibraryDetailScreenPresenter(
                                        dataSource = screen.datasource,
                                        navigator = navigator,
                                    ).present(),
                                )
                            }

                            entry<Screen.Search> {
                                SearchScreen(
                                    rememberSearchScreenPresenter(navigator).present(),
                                )
                            }
                        },
                )
            }
        }
    }
}

class RootNavigator constructor(
    private val backStack: NavBackStack<NavKey>,
) {
    val backStackList: List<NavKey>
        get() = backStack

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun popBackStack() {
        with(backStack) {
            if (backStack.size > 1) {
                removeAt(lastIndex)
            }
        }
    }
}
