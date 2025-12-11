/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.search

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.components.search.Search
import com.andannn.melodify.shared.compose.popup.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.Navigator

@Composable
internal fun SearchScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    presenter: Presenter<SearchScreenState> = rememberSearchScreenPresenter(navigator),
) {
    val state = presenter.present()
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 64.dp),
                hostState = rememberAndSetupSnackBarHostState(),
            )
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
}
