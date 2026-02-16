/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.components.lyrics.Lyrics
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiState
import com.andannn.melodify.shared.compose.components.play.control.rememberPlayerPresenter
import com.andannn.melodify.shared.compose.components.queue.PlayQueue
import com.andannn.melodify.shared.compose.components.tab.TabUi
import com.andannn.melodify.shared.compose.components.tab.TabUiState
import com.andannn.melodify.shared.compose.components.tab.content.TabContent
import com.andannn.melodify.shared.compose.components.tab.content.TabContentState
import com.andannn.melodify.shared.compose.components.tab.content.retainTabContentPresenter
import com.andannn.melodify.shared.compose.components.tab.retainTabUiPresenter
import com.andannn.melodify.shared.compose.popup.snackbar.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.components.playcontrol.DesktopPlayerUi
import com.andannn.melodify.windows.CustomMenuBar
import com.andannn.melodify.windows.WindowNavigator
import com.andannn.melodify.windows.WindowType
import com.andannn.melodify.windows.common.CommonActionDialog
import com.andannn.melodify.windows.handleMenuEvent
import java.awt.Dimension
import java.awt.GraphicsEnvironment

@Stable
data class MainUiState(
    val tabUiState: TabUiState,
    val playerUiState: PlayerUiState,
    val tabContentState: TabContentState,
)

@Composable
internal fun MainWindow(
    navigator: WindowNavigator,
    onCloseRequest: () -> Unit,
) {
    val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val dimension: Dimension = graphicsEnvironment.maximumWindowBounds.size
    val ratio = 0.8f
    val state =
        rememberWindowState(
            size = DpSize(dimension.width.times(ratio).dp, dimension.height.times(ratio).dp),
        )

    Window(
        state = state,
        onCloseRequest = onCloseRequest,
        title = "Melodify",
    ) {
        CustomMenuBar(navigator::handleMenuEvent)

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = rememberAndSetupSnackBarHostState(),
                    modifier = Modifier.padding(bottom = 64.dp),
                )
            },
        ) {
            val tabUiPresenter = retainTabUiPresenter()
            val tabState = tabUiPresenter.present()
            val eventSink =
                retain {
                    NavigationRequestEventSink()
                }
            val tabContentPresenter = retainTabContentPresenter(tabState.selectedTab, eventSink)
            val playerPresenter = rememberPlayerPresenter()
            val state =
                MainUiState(
                    tabUiState = tabState,
                    tabContentState = tabContentPresenter.present(),
                    playerUiState = playerPresenter.present(),
                )
            MainScreen(
                state = state,
                modifier = Modifier,
                onTabManagementClick = {
                    navigator.openWindow(WindowType.TabManage)
                },
            )
        }

        CommonActionDialog()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    state: MainUiState,
    modifier: Modifier = Modifier,
    onTabManagementClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(modifier = modifier.weight(1f)) {
            TabWithContentSector(
                tabUiState = state.tabUiState,
                tabContentState = state.tabContentState,
                modifier = Modifier.weight(2f),
                onTabManagementClick = onTabManagementClick,
            )

            VerticalDivider()

            RightPaneSector(
                modifier = Modifier.weight(1f),
            )
        }

        HorizontalDivider()

        DesktopPlayerUi(
            state = state.playerUiState,
            modifier = Modifier,
        )
    }
}

@Composable
private fun TabWithContentSector(
    tabUiState: TabUiState,
    tabContentState: TabContentState,
    modifier: Modifier = Modifier,
    onTabManagementClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier,
        ) {
            TabUi(tabUiState, onTabManagementClick = onTabManagementClick)

            TabContent(tabContentState)
        }
    }
}

enum class RightPageTab {
    PlayQueue,
    Lyrics,
}

@Composable
private fun RightPaneSector(modifier: Modifier) {
    var selectedTab by remember {
        mutableStateOf(RightPageTab.PlayQueue)
    }
    val selectedIndex by rememberUpdatedState(
        RightPageTab.entries.indexOf(selectedTab),
    )

    Column(modifier = modifier) {
        ScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = RightPageTab.entries.indexOf(selectedTab),
        ) {
            RightPageTab.entries.forEachIndexed { index, item ->
                Tab(
                    modifier = Modifier,
                    selected = index == selectedIndex,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    text = @Composable {
                        Text(
                            text = item.toString(),
                        )
                    },
                    onClick = {
                        selectedTab = item
                    },
                )
            }
        }

        when (selectedTab) {
            RightPageTab.Lyrics -> Lyrics()
            RightPageTab.PlayQueue -> PlayQueue()
        }
    }
}
