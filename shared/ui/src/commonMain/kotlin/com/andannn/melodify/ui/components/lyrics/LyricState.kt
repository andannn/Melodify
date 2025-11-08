/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.lyrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
sealed class LyricState {
    data object Idle : LyricState()

    data object Loading : LyricState()

    data object Error : LyricState()

    data object NoPlaying : LyricState()

    data class Loaded(
        val lyric: LyricModel?,
    ) : LyricState()
}

@Composable
fun rememberLyricPresenter(repository: Repository = LocalRepository.current): Presenter<LyricState> =
    retain(repository) {
        LyricPresenter(repository)
    }

private class LyricPresenter(
    private val repository: Repository,
) : ScopedPresenter<LyricState>() {
    private val currentPlayingAudioFlow =
        repository.playerStateMonitoryRepository
            .getPlayingMediaStateFlow()
            .stateIn(
                this,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null,
            )

    private var currentLoadState by
        mutableStateOf<LyricRepository.State?>(null)

    init {
        launch {
            currentPlayingAudioFlow.collect { currentPlayingAudio ->
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
        }
    }

    @Composable
    override fun present(): LyricState {
        val currentPlayingAudio by currentPlayingAudioFlow.collectAsStateWithLifecycle(null)
        if (currentPlayingAudio == null) {
            return LyricState.NoPlaying
        }

        return when (val state = currentLoadState) {
            is LyricRepository.State.Error -> LyricState.Error
            LyricRepository.State.Loading -> LyricState.Loading
            is LyricRepository.State.Success -> LyricState.Loaded(state.model)
            null -> LyricState.NoPlaying
        }
    }
}
