package com.andannn.melodify.ui.components.lyrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.LyricModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberLyricStateHolder(
    source: AudioItemModel?,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: Repository = getKoin().get(),
) = remember(
    scope,
    source,
    repository,
) {
    LyricStateHolder(
        scope = scope,
        source = source,
        repository = repository,
    )
}

class LyricStateHolder(
    scope: CoroutineScope,
    private val source: AudioItemModel?,
    private val repository: Repository,
) {
    var state by mutableStateOf<LyricState>(LyricState.Loading)

    init {
        if (source != null) {
            scope.launch {
                repository.lyricRepository.tryGetLyricOrIgnore(
                    mediaId = source.id,
                    trackName = source.name,
                    artistName = source.artist,
                    albumName = source.album,
                )
            }

            scope.launch {
                repository.lyricRepository.getLyricByMediaIdFlow(source.id).collect { lyricOrNull ->
                    state = LyricState.Loaded(lyricOrNull)
                }
            }
        }
    }
}

sealed class LyricState {
    data object Loading : LyricState()

    data class Loaded(val lyric: LyricModel?) : LyricState()
}
