/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
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
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.components.tab.TabUi
import com.andannn.melodify.ui.components.tabcontent.TabContent
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.andannn.melodify.ui.widgets.DropDownMenuIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeUiScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    homePresenter: Presenter<HomeState> = rememberHomeUiPresenter(navigator = navigator),
) {
    val homeState = homePresenter.present()
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
                    TopAppBarDefaults.topAppBarColors().run {
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
                    val options = MenuOption.entries
                    DropDownMenuIconButton(
                        options.map { it.textRes },
                        onSelectIndex = {
                            val selected = options[it]
                            homeState.eventSink.invoke(
                                HomeUiEvent.OnMenuSelected(
                                    selected = selected,
                                ),
                            )
                        },
                        imageVector = Icons.Rounded.MoreVert,
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
            TabUi(homeState.tabUiState, onTabManagementClick = {
                homeState.eventSink.invoke(HomeUiEvent.OnTabManagementClick)
            })

            TabContent(homeState.tabContentState, modifier = Modifier.padding(horizontal = 4.dp))
        }
    }

    Player()
    ActionDialogContainer()
}
