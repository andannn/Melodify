package com.andannn.melodify.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andannn.melodify.feature.home.tab.ReactiveTab
import com.andannn.melodify.feature.home.tab.TabUiStateHolder
import com.andannn.melodify.feature.home.tab.rememberTabUiStateHolder
import com.andannn.melodify.feature.home.tabcontent.TabContent
import com.andannn.melodify.feature.home.tabcontent.TabContentStateHolder
import com.andannn.melodify.feature.home.tabcontent.rememberTabContentStateHolder

const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeScreen(
    onNavigateCustomTabSetting: () -> Unit
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(
            onNavigateCustomTabSetting = onNavigateCustomTabSetting,
        )
    }
}

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onNavigateCustomTabSetting: () -> Unit = {},
) {
    HomeScreen(
        modifier = modifier,
        onSettingButtonClick = onNavigateCustomTabSetting
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettingButtonClick: () -> Unit = {},
) {
    val scrollBehavior = enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val tabUiStateHolder = rememberTabUiStateHolder(
        scope = scope
    )
    val tabContentStateHolder = rememberTabContentStateHolder(
        scope = scope,
        selectedTab = tabUiStateHolder.state.selectedTab
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors =
                TopAppBarDefaults.centerAlignedTopAppBarColors().run {
                    copy(scrolledContainerColor = containerColor)
                },
                title = {
                    Text(text = "Melodify")
                },
                actions = {
                    IconButton(
                        onClick = onSettingButtonClick,
                        content = {
                            Icon(Icons.Rounded.Settings, contentDescription = "")
                        }
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        TabWithContent(
            modifier = Modifier.padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            tabUiStateHolder = tabUiStateHolder,
            tabContentStateHolder = tabContentStateHolder
        )
    }
}

@Composable
fun TabWithContent(
    modifier: Modifier = Modifier,
    tabUiStateHolder: TabUiStateHolder,
    tabContentStateHolder: TabContentStateHolder,
) {
    Column(
        modifier = modifier
    ) {
        ReactiveTab(
            stateHolder = tabUiStateHolder
        )

        TabContent(
            stateHolder = tabContentStateHolder
        )
    }
}
