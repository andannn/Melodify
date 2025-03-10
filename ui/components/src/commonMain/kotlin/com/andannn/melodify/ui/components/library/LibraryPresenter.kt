package com.andannn.melodify.ui.components.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.andannn.melodify.ui.components.common.SearchScreen
import com.andannn.melodify.ui.components.common.newLibraryContentListScreen
import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter

class LibraryPresenter(
    private val navigator: Navigator
) : Presenter<LibraryState> {
    @Composable
    override fun present(): LibraryState {
        return LibraryState { eventSink ->
            when (eventSink) {
                is LibraryUiEvent.OnNavigateToLibraryContentList -> navigator.goTo(
                    newLibraryContentListScreen(eventSink.source)
                )

                LibraryUiEvent.Back -> navigator.pop()
                LibraryUiEvent.OnNavigateToSearch -> navigator.goTo(SearchScreen)
            }
        }
    }
}


@Stable
data class LibraryState(
    val evenSink: (LibraryUiEvent) -> Unit = {},
) : CircuitUiState

sealed interface LibraryUiEvent {
    data class OnNavigateToLibraryContentList(val source: LibraryDataSource) : LibraryUiEvent
    data object Back : LibraryUiEvent
    data object OnNavigateToSearch : LibraryUiEvent
}
