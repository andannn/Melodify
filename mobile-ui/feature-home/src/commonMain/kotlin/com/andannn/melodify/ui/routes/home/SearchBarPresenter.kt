/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.browsable
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequest
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.asLibraryDataSource
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.usecase.playMediaItems
import com.andannn.melodify.shared.compose.usecase.playOrGoToBrowsable
import io.github.aakira.napier.Napier
import io.github.andannn.popup.PopupHostState
import kotlinx.coroutines.launch

private const val TAG = "SearchWithContent"

@Composable
internal fun retainSearchBarPresenter(
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
    repository: Repository = LocalRepository.current,
) = retainPresenter(
    navigationRequestEventSink,
    popupHostState,
    repository,
) {
    SearchBarPresenter(
        navigationRequestEventSink,
        popupHostState,
        repository,
    )
}

@Stable
internal data class SearchBarLayoutState
    @OptIn(ExperimentalMaterial3Api::class)
    constructor(
        val currentContent: ContentState,
        val textFieldState: TextFieldState,
        val searchBarState: androidx.compose.material3.SearchBarState,
        val eventSink: (SearchBarUiEvent) -> Unit = {},
    )

@Stable
internal sealed interface ContentState {
    @Stable
    data class Search(
        val query: String,
    ) : ContentState

    @Stable
    data object Library : ContentState
}

internal sealed interface SearchBarUiEvent {
    data object OnBackFullScreen : SearchBarUiEvent

    data object OnExitSearch : SearchBarUiEvent

    data class OnSearchResultItemClick(
        val result: MediaItemModel,
    ) : SearchBarUiEvent

    data class OnSuggestionItemClick(
        val result: MediaItemModel,
    ) : SearchBarUiEvent

    data class OnConfirmSearch(
        val text: String,
    ) : SearchBarUiEvent
}

internal class SearchBarPresenter(
    private val navigationRequestEventSink: NavigationRequestEventSink,
    private val popupHostState: PopupHostState,
    private val repository: Repository,
) : RetainedPresenter<SearchBarLayoutState>() {
    private val contentState = mutableStateOf<ContentState>(ContentState.Library)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun present(): SearchBarLayoutState {
        val textFieldState = rememberTextFieldState()
        val searchBarState = rememberSearchBarState()
        val animationScope = rememberCoroutineScope()

        fun collapsedSearchScreen() {
            animationScope.launch {
                searchBarState.animateToCollapsed()
            }
        }

        fun exitSearch() {
            textFieldState.clearText()
            contentState.value = ContentState.Library
        }

        return SearchBarLayoutState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            currentContent = contentState.value,
        ) { event ->
            context(popupHostState, repository, navigationRequestEventSink) {
                when (event) {
                    is SearchBarUiEvent.OnSuggestionItemClick -> {
                        exitSearch()
                        collapsedSearchScreen()

                        retainedScope.launch {
                            playOrGoToBrowsable(event.result)
                            addToSearchHistory(event.result.name)
                        }
                    }

                    is SearchBarUiEvent.OnSearchResultItemClick -> {
                        collapsedSearchScreen()

                        retainedScope.launch {
                            playOrGoToBrowsable(event.result)
                        }
                    }

                    is SearchBarUiEvent.OnConfirmSearch -> {
                        animationScope.launch {
                            searchBarState.animateToCollapsed()
                        }
                        textFieldState.setTextAndPlaceCursorAtEnd(text = event.text)
                        contentState.value = ContentState.Search(event.text)

                        retainedScope.launch {
                            addToSearchHistory(event.text)
                        }
                    }

                    SearchBarUiEvent.OnExitSearch -> {
                        exitSearch()
                    }

                    SearchBarUiEvent.OnBackFullScreen -> {
                        exitSearch()
                        collapsedSearchScreen()
                    }
                }.also {
                    Napier.d(tag = TAG) { "on event: $event" }
                }
            }
        }
    }

    private suspend fun addToSearchHistory(text: String) {
        repository.addSearchHistory(text)
    }
}
