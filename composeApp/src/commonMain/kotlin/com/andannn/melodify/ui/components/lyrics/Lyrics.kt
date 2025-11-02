/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.ui.components.lyrics.content.PlainLyricsView
import com.andannn.melodify.ui.components.lyrics.content.SyncedLyrics
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter

@Composable
fun Lyrics(
    modifier: Modifier = Modifier,
    presenter: Presenter<LyricState> = rememberLyricPresenter(),
) {
    val state = presenter.present()
    LyricsViewContent(modifier = modifier.fillMaxSize(), lyricState = state)
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

@Composable
private fun LyricsViewContent(
    lyricState: LyricState,
    modifier: Modifier = Modifier,
) {
    when (lyricState) {
        is LyricState.Loaded -> {
            val lyricModel = lyricState.lyric
            if (lyricModel == null) {
                Box(modifier = modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "No lyrics found",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            } else {
                if (lyricModel.syncedLyrics.isNotBlank()) {
                    SyncedLyrics(
                        modifier = modifier,
                        syncedLyric = lyricModel.syncedLyrics,
                    )
                } else if (lyricModel.plainLyrics.isNotBlank()) {
                    PlainLyricsView(
                        modifier = modifier,
                        lyric = lyricModel.plainLyrics,
                    )
                }
            }
        }

        LyricState.Loading -> {
            Box(modifier = modifier) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        LyricState.NoPlaying -> {
            Box(modifier = modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Nothing playing",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }

        LyricState.Error -> {
            Box(modifier = modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineSmall,
                    text = "Lyric not found",
                )
            }
        }

        LyricState.Idle -> {}
    }
}

@Composable
private fun rememberLyricPresenter(repository: Repository = LocalRepository.current) =
    remember(repository) {
        LyricPresenter(repository)
    }

private class LyricPresenter(
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
