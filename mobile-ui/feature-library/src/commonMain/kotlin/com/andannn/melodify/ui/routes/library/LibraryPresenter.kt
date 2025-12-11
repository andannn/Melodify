/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.Screen

@Composable
internal fun retainLibraryPresenter(navigator: Navigator): Presenter<LibraryState> =
    retainPresenter {
        LibraryPresenter(
            navigator = navigator,
        )
    }

private class LibraryPresenter(
    private val navigator: Navigator,
) : RetainedPresenter<LibraryState>() {
    @Composable
    override fun present(): LibraryState =
        LibraryState { eventSink ->
            when (eventSink) {
                is LibraryUiEvent.OnNavigateToLibraryContentList -> {
                    navigator.navigateTo(
                        Screen.LibraryDetail(eventSink.source),
                    )
                }

                LibraryUiEvent.Back -> {
                    navigator.popBackStack()
                }

                LibraryUiEvent.OnNavigateToSearch -> {
                    navigator.navigateTo(Screen.Search)
                }
            }
        }
}

@Stable
internal data class LibraryState(
    val evenSink: (LibraryUiEvent) -> Unit = {},
)

internal sealed interface LibraryUiEvent {
    data class OnNavigateToLibraryContentList(
        val source: LibraryDataSource,
    ) : LibraryUiEvent

    data object Back : LibraryUiEvent

    data object OnNavigateToSearch : LibraryUiEvent
}
