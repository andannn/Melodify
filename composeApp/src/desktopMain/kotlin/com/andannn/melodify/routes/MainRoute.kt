package com.andannn.melodify.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andannn.melodify.ui.components.playcontrol.Player
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