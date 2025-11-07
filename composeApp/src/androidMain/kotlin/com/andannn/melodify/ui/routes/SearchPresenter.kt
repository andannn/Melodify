/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter

@Composable
fun rememberSearchScreenPresenter(navigator: Navigator): Presenter<SearchScreenState> =
    retain(navigator) {
        SearchScreenPresenter(navigator)
    }

@Stable
data class SearchScreenState(
    val eventSink: (SearchScreenEvent) -> Unit,
)

sealed interface SearchScreenEvent {
    data object OnBack : SearchScreenEvent

    data class OnNavigateToLibraryContentList(
        val source: LibraryDataSource,
    ) : SearchScreenEvent
}

private class SearchScreenPresenter(
    private val navigator: Navigator,
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
