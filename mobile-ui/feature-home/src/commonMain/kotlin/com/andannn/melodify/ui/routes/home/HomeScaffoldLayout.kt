/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.widgets.DropDownMenuIconButton
import com.andannn.melodify.shared.compose.components.search.suggestion.Suggestions
import com.andannn.melodify.shared.compose.popup.snackbar.rememberAndSetupSnackBarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScaffoldLayout(
    modifier: Modifier = Modifier,
    homeLayoutState: HomeLayoutState,
    enabled: Boolean = true,
    onMenuSelected: (MenuOption) -> Unit = {},
    onExitSelecting: () -> Unit = {},
    onMultiSelectionOptionClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val textFieldState = homeLayoutState.textFieldState
    val searchBarState = homeLayoutState.searchBarState
    val homeState = homeLayoutState.homeState
    val selectedMediaSet = homeLayoutState.selectedMediaSet
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    val inputField = @Composable {
        SearchBarInputField(homeLayoutState, enabled = enabled)
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
            if (homeState is HomeState.MultiSelecting) {
                MultiSelectingAppBar(
                    selectedCount = selectedMediaSet.size,
                    onExitSelecting = onExitSelecting,
                    onOptionClick = onMultiSelectionOptionClick,
                )
            } else {
                AppBarWithSearch(
                    modifier = Modifier.statusBarsPadding(),
                    scrollBehavior = scrollBehavior,
                    colors =
                        SearchBarDefaults.appBarWithSearchColors(
                            scrolledAppBarContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                    state = searchBarState,
                    inputField = inputField,
                    navigationIcon = {
                        if (homeState is HomeState.Search) {
                            NavigationBackButton(onBack = {
                                homeLayoutState.eventSink.invoke(HomeLayoutEvent.OnExitSearch)
                            })
                        } else {
                            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Rounded.MusicNote,
                                    contentDescription = ""
                                )
                            }
                        }
                    },
                    actions = {
                        DropDownMenuActionButton(
                            onSelectItem = onMenuSelected,
                        )
                    },
                )
                ExpandedFullScreenSearchBar(
                    state = searchBarState,
                    inputField = inputField,
                ) {
                    Suggestions(
                        query = textFieldState,
                        onConfirmSearch = {
                            homeLayoutState.eventSink.invoke(
                                HomeLayoutEvent.OnConfirmSearch(
                                    it,
                                ),
                            )
                        },
                        onResultItemClick = {
                            homeLayoutState.eventSink.invoke(
                                HomeLayoutEvent.OnSuggestionItemClick(
                                    it,
                                ),
                            )
                        },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
        ) {
            content()
        }
    }
}

@Composable
private fun NavigationBackButton(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    IconButton(modifier = modifier, onClick = onBack) {
        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
    }
}

@Composable
private fun DropDownMenuActionButton(
    modifier: Modifier = Modifier,
    onSelectItem: (MenuOption) -> Unit,
) {
    val options = MenuOption.entries
    DropDownMenuIconButton(
        modifier = modifier,
        options = options.map { it.textRes },
        onSelectIndex = {
            val selected = options[it]
            onSelectItem(selected)
        },
        imageVector = Icons.Rounded.MoreVert,
    )
}
