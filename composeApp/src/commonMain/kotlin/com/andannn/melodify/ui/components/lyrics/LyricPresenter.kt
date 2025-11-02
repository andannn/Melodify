/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.lyrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.LyricModel
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter

@Composable
fun rememberLyricPresenter(repository: Repository = LocalRepository.current) =
    remember(repository) {
        LyricPresenter(repository)
    }

class LyricPresenter(
    private val repository: Repository,
) : Presenter<LyricState> {
    @Composable
    override fun present(): LyricState {
        val currentPlayingAudio by repository.playerStateMonitoryRepository
            .getPlayingMediaStateFlow()
            .collectAsRetainedState(null)
        if (currentPlayingAudio == null) {
            return LyricState.NoPlaying
        }

        var currentLoadState by remember {
            mutableStateOf<LyricRepository.State?>(null)
        }

        LaunchedEffect(currentPlayingAudio) {
            currentLoadState = null

            val audio = currentPlayingAudio
            if (audio != null) {
                repository.lyricRepository
                    .getLyricByMediaIdFlow(
                        mediaId = audio.id,
                        trackName = audio.name,
                        artistName = audio.artist,
                        albumName = audio.album,
                    ).collect {
                        currentLoadState = it
                    }
            }
        }

        return when (val state = currentLoadState) {
            is LyricRepository.State.Error -> LyricState.Error
            LyricRepository.State.Loading -> LyricState.Loading
            is LyricRepository.State.Success -> LyricState.Loaded(state.model)
            null -> LyricState.NoPlaying
        }
    }
}

sealed class LyricState : CircuitUiState {
    data object Idle : LyricState()

    data object Loading : LyricState()

    data object Error : LyricState()

    data object NoPlaying : LyricState()

    data class Loaded(
        val lyric: LyricModel?,
    ) : LyricState()
}
