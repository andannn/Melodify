/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.andannn.melodify.ui.Nav3Screen
import com.andannn.melodify.ui.routes.HomeUiScreen
import com.andannn.melodify.ui.routes.rememberHomeUiPresenter
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
                    Nav3Screen.HomeScreen,
                )
            val navigator = remember(backStack) { RootNavigator(backStack) }

            CompositionLocalProvider(
                LocalRootNavigator provides navigator,
            ) {
                NavDisplay(
                    modifier = Modifier,
                    backStack = navigator.backStackList,
                    sceneStrategy = DialogSceneStrategy<NavKey>() then SinglePaneSceneStrategy(),
                    entryDecorators =
                        listOf(
                            rememberPopupControllerNavEntryDecorator(),
                            rememberRetainedValueStoreNavEntryDecorator(),
//                            rememberSceneSetupNavEntryDecorator(),
//                            rememberSavedStateNavEntryDecorator(),
//                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                    transitionSpec = {
                        // Slide in from right when navigating forward
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        // Slide in from left when navigating back
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                    },
                    entryProvider =
                        entryProvider {
                            entry<Nav3Screen.HomeScreen> {
                                HomeUiScreen(
                                    rememberHomeUiPresenter().present(),
                                )
                            }
                        },
                )
            }
        }
    }
}

val LocalRootNavigator =
    androidx.compose.runtime.staticCompositionLocalOf<RootNavigator> {
        error("No RootNavigator provided")
    }

class RootNavigator constructor(
    private val backStack: NavBackStack<NavKey>,
) {
    val backStackList: List<NavKey>
        get() = backStack

    fun navigateTo(screen: Nav3Screen) {
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
