package com.andannn.melodify.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andannn.melodify.ui.common.util.getCategoryResource
import com.andannn.melodify.ui.components.lyrics.LyricsView
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.components.queue.PlayQueue
import com.andannn.melodify.ui.components.tab.ReactiveTab
import com.andannn.melodify.ui.components.tab.rememberTabUiStateHolder
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentStateHolder
import com.andannn.melodify.ui.components.tabselector.CustomTabSelector

internal const val MAIN_ROUTE = "main_route"
fun NavGraphBuilder.mainRoute() {
    composable(route = MAIN_ROUTE) {
        MainRoute()
    }
}

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(modifier = modifier.weight(1f)) {
            LeftSidePaneSector(
                modifier = Modifier.weight(1f)
            )

            VerticalDivider()

            TabWithContentSector(
                modifier = Modifier.weight(2f)
            )

            VerticalDivider()

            RightPaneSector(
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()

        Player(
            modifier = Modifier
        )
    }
}

@Composable
private fun TabWithContentSector(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val tabUiStateHolder = rememberTabUiStateHolder(
        scope = scope
    )
    val tabContentStateHolder = rememberTabContentStateHolder(
        scope = scope,
        selectedTab = tabUiStateHolder.state.selectedTab
    )
    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier,
        ) {
            ReactiveTab(
                stateHolder = tabUiStateHolder
            )

            TabContent(
                stateHolder = tabContentStateHolder
            )
        }
    }
}

@Composable
private fun LeftSidePaneSector(
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        CustomTabSelector(
            modifier = Modifier
        )
    }
}

enum class RightPageTab {
    Lyrics,
    PlayQueue
}

@Composable
private fun RightPaneSector(
    modifier: Modifier
) {
    var selectedTab by remember {
        mutableStateOf(RightPageTab.Lyrics)
    }
    val selectedIndex by rememberUpdatedState(
        RightPageTab.entries.indexOf(selectedTab)
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
            RightPageTab.Lyrics -> LyricsView()

            RightPageTab.PlayQueue -> PlayQueue()
        }
    }
}