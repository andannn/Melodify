/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.components.lyrics.content.PlainLyricsView
import com.andannn.melodify.shared.compose.components.lyrics.content.SyncedLyrics

@Composable
fun Lyrics(
    modifier: Modifier = Modifier,
    presenter: Presenter<LyricState> = retainLyricPresenter(),
) {
    LyricsViewContent(
        modifier = modifier.fillMaxSize(),
        lyricState = presenter.present(),
    )
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
