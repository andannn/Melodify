/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.navigation.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.components.common.HomeScreen
import com.andannn.melodify.ui.components.common.LibraryScreen
import com.andannn.melodify.ui.components.common.SearchScreen
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.components.popup.dialog.ActionDialogContainer
import com.andannn.melodify.ui.components.popup.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.components.tab.TabUi
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tab.rememberTabUiPresenter
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.components.tabcontent.TabContentState
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentPresenter
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import io.github.aakira.napier.Napier

object HomeUiFactory : Ui.Factory {
    override fun create(
        screen: Screen,
        context: CircuitContext,
    ): Ui<*>? =
        when (screen) {
            is HomeScreen ->
                ui<HomeState> { state, modifier ->
                    HomeUiScreen(state, modifier)
                }

            else -> null
        }
}

object HomePresenterFactory : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? =
        when (screen) {
            is HomeScreen -> HomePresenter(navigator)
            else -> null
        }
}

private class HomePresenter(
    private val navigator: Navigator,
) : Presenter<HomeState> {
    @Composable
    override fun present(): HomeState {
        Napier.d(tag = "HomePresenter") { "HomePresenter present" }
        val tabUiPresenter = rememberTabUiPresenter(navigator)
        val tabUiState = tabUiPresenter.present()
        val tabContentPresenter = rememberTabContentPresenter(tabUiState.selectedTab)
        return HomeState(
            tabUiState = tabUiState,
            tabContentState = tabContentPresenter.present(),
        ) { eventSink ->
            when (eventSink) {
                HomeUiEvent.LibraryButtonClick -> navigator.goTo(LibraryScreen)
                HomeUiEvent.SearchButtonClick -> navigator.goTo(SearchScreen)
                HomeUiEvent.SettingButtonClick -> {}
            }
        }
    }
}

internal data class HomeState(
    val tabUiState: TabUiState,
    val tabContentState: TabContentState,
    val eventSink: (HomeUiEvent) -> Unit = {},
) : CircuitUiState

internal sealed interface HomeUiEvent {
    data object SettingButtonClick : HomeUiEvent

    data object SearchButtonClick : HomeUiEvent

    data object LibraryButtonClick : HomeUiEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeUiScreen(
    homeState: HomeState,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 64.dp),
                hostState = rememberAndSetupSnackBarHostState(),
            )
        },
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
                        onClick = { homeState.eventSink.invoke(HomeUiEvent.LibraryButtonClick) },
                        content = {
                            Icon(Icons.Rounded.Menu, contentDescription = "")
                        },
                    )
                },
                actions = {
                    IconButton(
                        onClick = { homeState.eventSink.invoke(HomeUiEvent.SearchButtonClick) },
                        content = {
                            Icon(Icons.Rounded.Search, contentDescription = "")
                        },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
        ) {
            TabUi(homeState.tabUiState)

            TabContent(homeState.tabContentState)
        }
    }

    Player()
    ActionDialogContainer()
}
