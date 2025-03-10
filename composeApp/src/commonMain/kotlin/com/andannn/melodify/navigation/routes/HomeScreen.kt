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
import com.andannn.melodify.ui.components.tab.ReactiveTab
import com.andannn.melodify.ui.components.tab.TabUiPresenter
import com.andannn.melodify.ui.components.tab.TabUiPresenterFactory
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentStateHolder
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui

const val HOME_ROUTE = "home_route"

object HomeUiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is HomeScreen -> ui<HomeState> { state, modifier ->
                HomeUiScreen(
                    state,
                    modifier
                )
            }

            else -> null
        }
    }
}

object HomePresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext
    ): Presenter<*>? {
        return when (screen) {
            is HomeScreen -> HomePresenter(
                tabUiState = TabUiPresenterFactory.create(screen, navigator, context)
            )

            else -> null
        }

    }
}

private class HomePresenter(
    private val tabUiState: TabUiPresenter
) : Presenter<HomeState> {
    @Composable
    override fun present(): HomeState {
        return HomeState(
            tabUiState = tabUiState.present()
        )
    }
}

private data class HomeState(
    val tabUiState: TabUiState,
) : CircuitUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeUiScreen(
    homeState: HomeState,
    modifier: Modifier = Modifier,
    onSettingButtonClick: () -> Unit = {},
    onSearchButtonClick: () -> Unit = {},
    onLibraryButtonClick: () -> Unit = {},
) {
    val scrollBehavior = enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val tabContentStateHolder = rememberTabContentStateHolder(
        scope = scope,
        selectedTab = homeState.tabUiState.selectedTab
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
                homeState.tabUiState
            )

            TabContent(
                stateHolder = tabContentStateHolder
            )
        }
    }
}
