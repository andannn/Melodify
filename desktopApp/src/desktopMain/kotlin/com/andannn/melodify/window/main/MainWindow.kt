/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.window.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.andannn.melodify.app.MelodifyDeskTopAppState
import com.andannn.melodify.ui.components.lyrics.Lyrics
import com.andannn.melodify.ui.components.playcontrol.DesktopPlayerUi
import com.andannn.melodify.ui.components.playcontrol.PlayerUiState
import com.andannn.melodify.ui.components.playcontrol.rememberPlayerPresenter
import com.andannn.melodify.ui.components.queue.PlayQueue
import com.andannn.melodify.ui.components.tab.TabUi
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tab.rememberTabUiPresenter
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.components.tabcontent.TabContentState
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentPresenter
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.andannn.melodify.window.CustomMenuBar
import com.andannn.melodify.window.rememberCommonWindowState
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
    appState: MelodifyDeskTopAppState,
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
        val windowState = rememberCommonWindowState()

        CustomMenuBar(appState)

        Scaffold(
            snackbarHost = {
                SnackbarHost(windowState.snackBarHostState)
            },
        ) {
            val tabUiPresenter = rememberTabUiPresenter()
            val tabState = tabUiPresenter.present()
            val tabContentPresenter = rememberTabContentPresenter(tabState.selectedTab)
            val playerPresenter = rememberPlayerPresenter()
            val state =
                MainUiState(
                    tabUiState = tabState,
                    tabContentState = tabContentPresenter.present(),
                    playerUiState = playerPresenter.present(),
                )
            MainScreen(state, Modifier)
        }

        ActionDialogContainer()
    }
}

@Composable
fun MainScreen(
    state: MainUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(modifier = modifier.weight(1f)) {
            TabWithContentSector(
                state.tabUiState,
                state.tabContentState,
                modifier = Modifier.weight(2f),
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
) {
    Surface(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier,
        ) {
            TabUi(tabUiState)

            TabContent(tabContentState)
        }
    }
}

enum class RightPageTab {
    Lyrics,
    PlayQueue,
}

@Composable
private fun RightPaneSector(modifier: Modifier) {
    var selectedTab by remember {
        mutableStateOf(RightPageTab.Lyrics)
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
