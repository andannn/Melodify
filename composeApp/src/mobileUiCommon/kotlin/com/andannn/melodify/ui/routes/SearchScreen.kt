/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.LibraryDetailScreen
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.components.search.Search
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter

class SearchScreenPresenter(
    private val navigator: Navigator,
) : Presenter<SearchScreenState> {
    @Composable
    override fun present(): SearchScreenState =
        SearchScreenState { event ->
            when (event) {
                SearchScreenEvent.OnBack -> navigator.pop()

                is SearchScreenEvent.OnNavigateToLibraryContentList ->
                    navigator.goTo(
                        LibraryDetailScreen(event.source),
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
