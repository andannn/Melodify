/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.lyrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
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
fun retainLyricPresenter(repository: Repository = LocalRepository.current): Presenter<LyricState> =
    retainPresenter(repository) {
        LyricsPresenter(repository)
    }

private class LyricsPresenter(
    private val repository: Repository,
) : RetainedPresenter<LyricState>() {
    private val currentPlayingAudioFlow =
        repository
            .getPlayingMediaStateFlow()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null,
            )

    private var currentLoadState by
        mutableStateOf<LyricRepository.State?>(null)

    init {
        retainedScope.launch {
            currentPlayingAudioFlow.collect { currentPlayingAudio ->
                currentLoadState = null

                val audio = currentPlayingAudio as? AudioItemModel
                if (audio != null) {
                    repository
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
