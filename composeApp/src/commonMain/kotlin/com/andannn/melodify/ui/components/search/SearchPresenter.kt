/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.core.data.MediaContentRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.internal.MediaControllerRepository
import com.andannn.melodify.core.data.internal.PlayerStateMonitoryRepository
import com.andannn.melodify.core.data.internal.UserPreferenceRepository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberSearchUiPresenter(repository: Repository = LocalRepository.current) =
    remember(
        repository,
    ) {
        SearchUiPresenter(
            repository.mediaContentRepository,
            repository.userPreferenceRepository,
            repository.mediaControllerRepository,
            repository.playerStateMonitoryRepository,
        )
    }

class SearchUiPresenter(
    private val contentLibrary: MediaContentRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val mediaControllerRepository: MediaControllerRepository,
    private val playerStateMonitoryRepository: PlayerStateMonitoryRepository,
) : Presenter<SearchUiState> {
    @Composable
    override fun present(): SearchUiState {
        val scope = rememberCoroutineScope()

        var searchTextField by rememberRetained {
            mutableStateOf(TextFieldState())
        }

        var searchResult by rememberRetained {
            mutableStateOf<SearchState>(SearchState.Init)
        }

        var expanded by rememberSaveable { mutableStateOf(true) }

        return SearchUiState(
            searchState = searchResult,
            isExpand = expanded,
            inputText = searchTextField,
        ) { eventSink ->
            when (eventSink) {
                is SearchUiEvent.OnConfirmSearch -> {
                    searchTextField = TextFieldState(eventSink.text)
                    expanded = false

                    scope.launch {
                        val searchText = searchTextField.text.toString()
                        if (searchText.isEmpty()) return@launch

                        searchResult = SearchState.Searching

                        searchResult =
                            if (isValid(searchText)) {
                                val result = contentLibrary.searchContent(searchText)
                                if (result.isEmpty()) {
                                    SearchState.NoObject
                                } else {
                                    toResult(result)
                                }
                            } else {
                                SearchState.Result(emptyList())
                            }
                    }

                    scope.launch {
                        userPreferenceRepository.addSearchHistory(eventSink.text)
                    }
                }

                is SearchUiEvent.OnPlayAudio -> onPlayAudio(scope, eventSink.audioItemModel)
                is SearchUiEvent.OnExpandChange -> {
                    expanded = eventSink.isExpand
                }
            }
        }
    }

    private fun onPlayAudio(
        scope: CoroutineScope,
        audioItemModel: AudioItemModel,
    ) {
        scope.launch {
            val isQueueEmpty = playerStateMonitoryRepository.getPlayListQueue().isEmpty()
            if (!isQueueEmpty) {
                // add audio item to queue and play this audio item
                val currentIndex = playerStateMonitoryRepository.getPlayingIndexInQueue()
                val newToPlayIndex = currentIndex + 1
                mediaControllerRepository.addMediaItems(newToPlayIndex, listOf(audioItemModel))
                mediaControllerRepository.seekMediaItem(newToPlayIndex)
                mediaControllerRepository.play()
            } else {
                // play audio item
                mediaControllerRepository.playMediaList(listOf(audioItemModel))
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

data class SearchUiState(
    val inputText: TextFieldState = TextFieldState(),
    val isExpand: Boolean = true,
    val searchState: SearchState = SearchState.Init,
    val eventSink: (SearchUiEvent) -> Unit = {},
) : CircuitUiState

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
