package com.andannn.melodify.ui.components.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Lyrics(
    presenter: LyricPresenter = rememberLyricPresenter(),
    modifier: Modifier = Modifier,
) {
    val state = presenter.present()
    LyricsViewContent(modifier = modifier.fillMaxSize(), lyricState = state)
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
    }
}

@Composable
private fun PlainLyricsView(
    lyric: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        text = lyric,
    )
}
