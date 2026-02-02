/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.widgets.DropDownMenuIconButton
import com.andannn.melodify.shared.compose.components.play.control.ResumePointIndicatorContainer
import com.andannn.melodify.shared.compose.components.search.suggestion.Suggestions
import com.andannn.melodify.shared.compose.components.tab.TabUi
import com.andannn.melodify.shared.compose.components.tab.content.TabContent
import com.andannn.melodify.shared.compose.popup.snackbar.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun HomeUiScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    homePresenter: Presenter<HomeState> = retainHomeUiPresenter(navigator = navigator),
) {
    val homeState = homePresenter.present()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState),
        )
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = {
                    SearchLeadingIcon(searchBarState, scope)
                },
            )
        }
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 64.dp),
                hostState = rememberAndSetupSnackBarHostState(),
            )
        },
        topBar = {
            AppBarWithSearch(
                modifier = Modifier.statusBarsPadding(),
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = {
                    IconButton(
                        onClick = { homeState.eventSink.invoke(HomeUiEvent.LibraryButtonClick) },
                        content = {
                            Icon(Icons.Rounded.Menu, contentDescription = "")
                        },
                    )
                },
                actions = {
                    val options = MenuOption.entries
                    DropDownMenuIconButton(
                        options = options.map { it.textRes },
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
            )
            ExpandedFullScreenContainedSearchBar(
                state = searchBarState,
                inputField = inputField,
                colors = appBarWithSearchColors.searchBarColors,
            ) {
                Suggestions(
                    query = textFieldState,
                    onConfirmSearch = {
                    },
                    onResultItemClick = {
                    },
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
        ) {
            TabUi(
                state = homeState.tabUiState,
                onTabManagementClick = {
                    homeState.eventSink.invoke(HomeUiEvent.OnTabManagementClick)
                },
            )

            TabContent(homeState.tabContentState, modifier = Modifier)
        }
    }

    ResumePointIndicatorContainer(
        modifier =
            Modifier
                .zIndex(1f),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchLeadingIcon(
    searchBarState: SearchBarState,
    scope: CoroutineScope,
) = if (searchBarState.currentValue == SearchBarValue.Expanded) {
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { PlainTooltip { Text("Back") } },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
        }
    }
} else {
    Icon(Icons.Default.Search, contentDescription = null)
}
