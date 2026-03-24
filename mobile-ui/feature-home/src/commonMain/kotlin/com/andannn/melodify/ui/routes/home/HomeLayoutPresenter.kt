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
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.entry.option.MediaOptionDialogResult
import com.andannn.melodify.shared.compose.popup.entry.option.OptionItem
import com.andannn.melodify.shared.compose.popup.entry.option.OptionPopup
import com.andannn.melodify.shared.compose.popup.snackbar.LocalSnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController
import com.andannn.melodify.shared.compose.usecase.addToNextPlay
import com.andannn.melodify.shared.compose.usecase.addToPlaylist
import com.andannn.melodify.shared.compose.usecase.addToQueue
import com.andannn.melodify.shared.compose.usecase.deleteItems
import com.andannn.melodify.shared.compose.usecase.playOrGoToBrowsable
import io.github.andannn.popup.PopupHostState
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "SearchWithContent"

@Composable
internal fun retainHomeLayoutPresenter(
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
    snackBarController: SnackBarController = LocalSnackBarController.current,
    mediaFileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
    repository: Repository = LocalRepository.current,
) = retainPresenter(
    navigationRequestEventSink,
    popupHostState,
    snackBarController,
    mediaFileDeleteHelper,
    repository,
) {
    HomeLayoutPresenter(
        navigationRequestEventSink,
        popupHostState,
        snackBarController,
        mediaFileDeleteHelper,
        repository,
    )
}

@Stable
internal data class HomeLayoutState
    @OptIn(ExperimentalMaterial3Api::class)
    constructor(
        val homeState: HomeState,
        val textFieldState: TextFieldState,
        val searchBarState: androidx.compose.material3.SearchBarState,
        val selectedMediaSet: Set<MediaItemModel>,
        val eventSink: (HomeLayoutEvent) -> Unit = {},
    )

@Stable
internal sealed interface HomeState {
    @Stable
    data class Search(
        val query: String,
    ) : HomeState

    @Stable
    data object Library : HomeState

    @Stable
    data object MultiSelecting : HomeState
}

internal sealed interface HomeLayoutEvent {
    data object OnBackFullScreen : HomeLayoutEvent

    data object OnExitSearch : HomeLayoutEvent

    data class OnSearchResultItemClick(
        val result: MediaItemModel,
    ) : HomeLayoutEvent

    data class OnSuggestionItemClick(
        val result: MediaItemModel,
    ) : HomeLayoutEvent

    data class OnConfirmSearch(
        val text: String,
    ) : HomeLayoutEvent

    data class OnClickMediaItemWhenSelecting(
        val mediaItem: MediaItemModel,
    ) : HomeLayoutEvent

    data object OnExitSelecting : HomeLayoutEvent

    data object OnMultiSelectionOptionClick : HomeLayoutEvent
}

internal class HomeLayoutPresenter(
    private val navigationRequestEventSink: NavigationRequestEventSink,
    private val popupHostState: PopupHostState,
    private val snackBarController: SnackBarController,
    private val mediaFileDeleteHelper: MediaFileDeleteHelper,
    private val repository: Repository,
) : RetainedPresenter<HomeLayoutState>() {
    private val homeState = mutableStateOf<HomeState>(HomeState.Library)
    private val selectedMediaSet = mutableStateSetOf<MediaItemModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun present(): HomeLayoutState {
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
            homeState.value = HomeState.Library
        }

        return HomeLayoutState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            selectedMediaSet = selectedMediaSet,
            homeState = homeState.value,
        ) { event ->
            context(popupHostState, repository, navigationRequestEventSink) {
                when (event) {
                    is HomeLayoutEvent.OnSuggestionItemClick -> {
                        exitSearch()
                        collapsedSearchScreen()

                        retainedScope.launch {
                            playOrGoToBrowsable(event.result)
                            addToSearchHistory(event.result.name)
                        }
                    }

                    is HomeLayoutEvent.OnSearchResultItemClick -> {
                        collapsedSearchScreen()

                        retainedScope.launch {
                            playOrGoToBrowsable(event.result)
                        }
                    }

                    is HomeLayoutEvent.OnConfirmSearch -> {
                        animationScope.launch {
                            searchBarState.animateToCollapsed()
                        }
                        textFieldState.setTextAndPlaceCursorAtEnd(text = event.text)
                        homeState.value = HomeState.Search(event.text)

                        retainedScope.launch {
                            addToSearchHistory(event.text)
                        }
                    }

                    HomeLayoutEvent.OnExitSearch -> {
                        exitSearch()
                    }

                    HomeLayoutEvent.OnBackFullScreen -> {
                        exitSearch()
                        collapsedSearchScreen()
                    }

                    is HomeLayoutEvent.OnClickMediaItemWhenSelecting -> {
                        if (selectedMediaSet.contains(event.mediaItem)) {
                            // remove selected item
                            val state = homeState.value
                            if (state !is HomeState.MultiSelecting) {
                                throw IllegalStateException("remove selected item ${event.mediaItem} in $state.")
                            } else {
                                selectedMediaSet.remove(event.mediaItem)
                                if (selectedMediaSet.isEmpty()) {
                                    homeState.value = HomeState.Library
                                }
                            }
                        } else {
                            homeState.value = HomeState.MultiSelecting
                            selectedMediaSet.add(event.mediaItem)
                        }
                    }

                    HomeLayoutEvent.OnExitSelecting -> {
                        homeState.value = HomeState.Library
                        selectedMediaSet.clear()
                    }

                    HomeLayoutEvent.OnMultiSelectionOptionClick -> {
                        retainedScope.launch {
                            showMultiSelectionOption()
                        }
                    }
                }
            }
        }
    }

    private fun exitMultiSelectingMode() {
        homeState.value = HomeState.Library
        selectedMediaSet.clear()
    }

    private suspend fun showMultiSelectionOption() {
        val result =
            popupHostState.showDialog(
                OptionPopup(
                    options =
                        buildList {
                            add(OptionItem.PLAY_NEXT)
                            add(OptionItem.ADD_TO_QUEUE)
                            add(OptionItem.ADD_TO_PLAYLIST)
                            add(OptionItem.DELETE_MEDIA_FILE)
                        },
                ),
            )

        if (result is MediaOptionDialogResult.ClickOptionItemResult) {
            val currentSelection = selectedMediaSet.toList()

            exitMultiSelectingMode()

            context(repository, popupHostState, snackBarController, mediaFileDeleteHelper) {
                when (result.optionItem) {
                    OptionItem.DELETE_MEDIA_FILE -> {
                        deleteItems(currentSelection)
                    }

                    OptionItem.PLAY_NEXT -> {
                        addToNextPlay(currentSelection)
                    }

                    OptionItem.ADD_TO_QUEUE -> {
                        addToQueue(currentSelection)
                    }

                    OptionItem.ADD_TO_PLAYLIST -> {
                        addToPlaylist(currentSelection)
                    }

                    else -> {}
                }
            }
        }
    }

    private suspend fun addToSearchHistory(text: String) {
        repository.addSearchHistory(text)
    }
}
