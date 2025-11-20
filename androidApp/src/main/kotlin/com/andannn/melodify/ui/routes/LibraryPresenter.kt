/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.ui.core.retainPresenter

@Composable
fun retainLibraryPresenter(navigator: Navigator): Presenter<LibraryState> =
    retainPresenter {
        LibraryPresenter(
            navigator = navigator,
        )
    }

private class LibraryPresenter(
    private val navigator: Navigator,
) : ScopedPresenter<LibraryState>() {
    @Composable
    override fun present(): LibraryState =
        LibraryState { eventSink ->
            when (eventSink) {
                is LibraryUiEvent.OnNavigateToLibraryContentList ->
                    navigator.navigateTo(
                        Screen.LibraryDetail(eventSink.source),
                    )

                LibraryUiEvent.Back -> navigator.popBackStack()
                LibraryUiEvent.OnNavigateToSearch -> navigator.navigateTo(Screen.Search)
            }
        }
}

@Stable
data class LibraryState(
    val evenSink: (LibraryUiEvent) -> Unit = {},
)

sealed interface LibraryUiEvent {
    data class OnNavigateToLibraryContentList(
        val source: LibraryDataSource,
    ) : LibraryUiEvent

    data object Back : LibraryUiEvent

    data object OnNavigateToSearch : LibraryUiEvent
}
