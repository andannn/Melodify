/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.Screen

@Composable
internal fun rememberSearchScreenPresenter(navigator: Navigator): Presenter<SearchScreenState> =
    retain(navigator) {
        SearchScreenPresenter(navigator)
    }

@Stable
internal data class SearchScreenState(
    val eventSink: (SearchScreenEvent) -> Unit,
)

internal sealed interface SearchScreenEvent {
    data object OnBack : SearchScreenEvent

    data class OnNavigateToLibraryContentList(
        val source: LibraryDataSource,
    ) : SearchScreenEvent
}

private class SearchScreenPresenter(
    private val navigator: Navigator,
) : RetainedPresenter<SearchScreenState>() {
    @Composable
    override fun present(): SearchScreenState =
        SearchScreenState { event ->
            when (event) {
                SearchScreenEvent.OnBack -> {
                    navigator.popBackStack()
                }

                is SearchScreenEvent.OnNavigateToLibraryContentList -> {
                    navigator.navigateTo(
                        Screen.LibraryDetail(event.source),
                    )
                }
            }
        }
}
