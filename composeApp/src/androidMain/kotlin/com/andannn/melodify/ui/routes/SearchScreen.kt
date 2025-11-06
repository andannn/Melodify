/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import com.andannn.melodify.RootNavigator
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.components.search.Search
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.slack.circuit.runtime.CircuitUiState

@Composable
fun rememberSearchScreenPresenter(navigator: RootNavigator) =
    retain(navigator) {
        SearchScreenPresenter(navigator)
    }

class SearchScreenPresenter(
    private val navigator: RootNavigator,
) : ScopedPresenter<SearchScreenState>() {
    @Composable
    override fun present(): SearchScreenState =
        SearchScreenState { event ->
            when (event) {
                SearchScreenEvent.OnBack -> navigator.popBackStack()

                is SearchScreenEvent.OnNavigateToLibraryContentList ->
                    navigator.navigateTo(
                        Screen.LibraryDetail(event.source),
                    )
            }
        }
}

@Composable
fun SearchScreen(
    state: SearchScreenState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(rememberAndSetupSnackBarHostState())
        },
    ) {
        Search(
            onBackKeyPressed = {
                state.eventSink(SearchScreenEvent.OnBack)
            },
            onNavigateToLibraryDetail = {
                state.eventSink(SearchScreenEvent.OnNavigateToLibraryContentList(it))
            },
        )
    }

    Player()
    ActionDialogContainer()
}

data class SearchScreenState(
    val eventSink: (SearchScreenEvent) -> Unit,
) : CircuitUiState

sealed interface SearchScreenEvent {
    data object OnBack : SearchScreenEvent

    data class OnNavigateToLibraryContentList(
        val source: LibraryDataSource,
    ) : SearchScreenEvent
}
