/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.popup.LocalPopupController
import com.andannn.melodify.shared.compose.popup.PopupController
import com.andannn.melodify.shared.compose.usecase.playMediaItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun rememberSearchUiPresenter(
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
) = retainPresenter(
    repository,
) {
    SearchUiPresenter(
        repository,
        popupController,
    )
}

@Stable
data class SearchUiState(
    val focusRequester: FocusRequester = FocusRequester(),
    val inputText: TextFieldState = TextFieldState(),
    val isExpand: Boolean = true,
    val searchState: SearchState = SearchState.Init,
    val eventSink: (SearchUiEvent) -> Unit = {},
)

sealed interface SearchUiEvent {
    data class OnPlayAudio(
        val audioItemModel: AudioItemModel,
    ) : SearchUiEvent

    data class OnConfirmSearch(
        val text: String,
    ) : SearchUiEvent

    data class OnExpandChange(
        val isExpand: Boolean,
    ) : SearchUiEvent
}

@Stable
sealed interface SearchState {
    data object Init : SearchState

    data object Searching : SearchState

    data class Result(
        val albums: List<AlbumItemModel> = emptyList(),
        val artists: List<ArtistItemModel> = emptyList(),
        val audios: List<AudioItemModel> = emptyList(),
    ) : SearchState

    data object NoObject : SearchState
}

private class SearchUiPresenter(
    private val repository: Repository,
    private val popupController: PopupController,
) : RetainedPresenter<SearchUiState>() {
    private var searchTextField by mutableStateOf(TextFieldState())
    private var searchResult by mutableStateOf<SearchState>(SearchState.Init)

    private val focusRequester = FocusRequester()

    init {
        retainedScope.launch {
            delay(50)
            focusRequester.requestFocus()
        }
    }

    @Composable
    override fun present(): SearchUiState {
        var expanded by rememberSaveable { mutableStateOf(true) }

        return SearchUiState(
            focusRequester = focusRequester,
            searchState = searchResult,
            isExpand = expanded,
            inputText = searchTextField,
        ) { eventSink ->
            when (eventSink) {
                is SearchUiEvent.OnConfirmSearch -> {
                    searchTextField = TextFieldState(eventSink.text)
                    expanded = false

                    retainedScope.launch {
                        val searchText = searchTextField.text.toString()
                        if (searchText.isEmpty()) return@launch

                        searchResult = SearchState.Searching

                        searchResult =
                            if (isValid(searchText)) {
                                val result = repository.searchContent(searchText)
                                if (result.isEmpty()) {
                                    SearchState.NoObject
                                } else {
                                    toResult(result)
                                }
                            } else {
                                SearchState.Result(emptyList())
                            }
                    }

                    retainedScope.launch {
                        repository.addSearchHistory(eventSink.text)
                    }
                }

                is SearchUiEvent.OnPlayAudio -> {
                    onPlayAudio(eventSink.audioItemModel)
                }

                is SearchUiEvent.OnExpandChange -> {
                    expanded = eventSink.isExpand
                }
            }
        }
    }

    private fun onPlayAudio(audioItemModel: AudioItemModel) {
        retainedScope.launch {
            context(repository, popupController) {
                playMediaItems(
                    audioItemModel,
                    listOf(audioItemModel),
                )
            }
        }
    }

    private fun isValid(text: String) = text.isNotBlank()

    private fun toResult(result: List<MediaItemModel>) =
        SearchState.Result(
            albums = result.filterIsInstance<AlbumItemModel>(),
            artists = result.filterIsInstance<ArtistItemModel>(),
            audios = result.filterIsInstance<AudioItemModel>(),
        )
}
