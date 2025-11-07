/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.components.search.Search
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer

@Composable
fun SearchScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    presenter: Presenter<SearchScreenState> = rememberSearchScreenPresenter(navigator),
) {
    val state = presenter.present()
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(rememberAndSetupSnackBarHostState())
        },
    ) {
        // ignore padding
        it
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
