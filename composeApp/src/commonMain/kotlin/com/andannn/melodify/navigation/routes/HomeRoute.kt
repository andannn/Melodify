package com.andannn.melodify.navigation.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
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
import com.andannn.melodify.ui.components.tab.ReactiveTab
import com.andannn.melodify.ui.components.tab.rememberTabUiStateHolder
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentStateHolder

const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeScreen(
    onNavigateCustomTabSetting: () -> Unit,
    onNavigateSearchPage: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(
            onSettingButtonClick = onNavigateCustomTabSetting,
            onSearchButtonClick = onNavigateSearchPage,
            onLibraryButtonClick = onNavigateToLibrary,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    onSettingButtonClick: () -> Unit = {},
    onSearchButtonClick: () -> Unit = {},
    onLibraryButtonClick: () -> Unit = {},
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
                navigationIcon = {
                    IconButton(
                        onClick = onLibraryButtonClick,
                        content = {
                            Icon(Icons.Rounded.Menu, contentDescription = "")
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSearchButtonClick,
                        content = {
                            Icon(Icons.Rounded.Search, contentDescription = "")
                        }
                    )
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
        Column(
            modifier = Modifier.padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
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
