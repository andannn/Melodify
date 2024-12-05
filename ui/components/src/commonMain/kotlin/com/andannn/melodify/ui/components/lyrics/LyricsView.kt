package com.andannn.melodify.ui.components.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.repository.PlayerStateMonitoryRepository
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun LyricsView(
    modifier: Modifier = Modifier,
) {
    val source: AudioItemModel? by getKoin().get<PlayerStateMonitoryRepository>()
        .getPlayingMediaStateFlow().collectAsState(null)

    if (source != null) {
        val lyricStateHolder: LyricStateHolder = rememberLyricStateHolder(source)

        LyricsViewContent(
            modifier = modifier,
            lyricState = lyricStateHolder.state,
        )
    } else {
        Box(modifier = modifier.fillMaxHeight()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Nothing playing",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
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
