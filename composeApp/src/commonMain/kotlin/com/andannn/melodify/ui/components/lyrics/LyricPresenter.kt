/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.lyrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.ui.components.common.LocalRepository
import com.slack.circuit.retained.collectAsRetainedState
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

        LaunchedEffect(currentPlayingAudio) {
            val audio = currentPlayingAudio
            if (audio != null) {
                repository.lyricRepository.tryGetLyricOrIgnore(
                    mediaId = audio.id,
                    trackName = audio.name,
                    artistName = audio.artist,
                    albumName = audio.album,
                )
            }
        }

        val lyric by repository.lyricRepository
            .getLyricByMediaIdFlow(currentPlayingAudio?.id ?: "")
            .collectAsRetainedState(null)
        return if (lyric == null) LyricState.Loading else LyricState.Loaded(lyric)
    }
}

sealed class LyricState : CircuitUiState {
    data object Loading : LyricState()

    data object NoPlaying : LyricState()

    data class Loaded(
        val lyric: LyricModel?,
    ) : LyricState()
}
