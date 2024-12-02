package com.andannn.melodify.feature.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.feature.common.widgets.CircleBorderImage
import com.andannn.melodify.feature.player.util.getIcon
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlayerSector(
    playerStateViewModel: PlayerStateViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState = playerStateViewModel.playerUiStateFlow.collectAsState()
    when (val state = uiState.value) {
        is PlayerUiState.Active -> PlayStateBar(
            modifier = modifier,
            coverUri = state.mediaItem.artWorkUri,
            playMode = state.playMode,
            isShuffle = state.isShuffle,
            isPlaying = state.isPlaying,
            isFavorite = state.isFavorite,
            playListQueue = state.playListQueue,
            activeMediaItem = state.mediaItem,
            title = state.mediaItem.name,
            artist = state.mediaItem.artist,
            progress = state.progress,
            duration = state.duration,
            onEvent = playerStateViewModel::onEvent,
        )

        PlayerUiState.Inactive -> PlayStateBar(
            modifier = modifier,
            coverUri = "",
            activeMediaItem = AudioItemModel.DEFAULT,
        )
    }
}


@Composable
fun PlayStateBar(
    coverUri: String,
    activeMediaItem: AudioItemModel,
    playListQueue: List<AudioItemModel> = emptyList(),
    modifier: Modifier = Modifier,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    isShuffle: Boolean = false,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    title: String = "",
    artist: String = "",
    progress: Float = 1f,
    duration: Long = 0L,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    Surface(
        modifier = modifier,
    ) {
        Column {
            Row(
                modifier = Modifier.height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlayInfoWithAlbumCover(
                    modifier = Modifier.weight(1f),
                    coverUri = coverUri,
                    title = title,
                    artist = artist
                )

                PlayControlBar(
                    modifier = Modifier,
                    enabled = true,
                    isPlaying = isPlaying,
                    playMode = playMode,
                    isShuffle = isShuffle,
                    onEvent = onEvent,
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            Slider(
                modifier = Modifier.height(24.dp),
                value = progress,
                enabled = true,
                onValueChange = {
                    onEvent(PlayerUiEvent.OnProgressChange(it))
                },
            )
        }
    }
}

@Composable
internal fun PlayControlBar(
    modifier: Modifier = Modifier,
    isShuffle: Boolean,
    isPlaying: Boolean,
    enabled: Boolean,
    playMode: PlayMode,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    Row(modifier = modifier) {
        IconButton(
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnShuffleButtonClick)
            },
        ) {
            Icon(
                if (isShuffle) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle,
                contentDescription = "",
            )
        }

        IconButton(
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnNextButtonClick)
            },
        ) {
            Icon(
                Icons.Rounded.SkipPrevious,
                contentDescription = "",
            )
        }

        IconButton(
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnPlayButtonClick)
            },
        ) {
            if (isPlaying) {
                Icon(imageVector = Icons.Rounded.Pause, contentDescription = "")
            } else {
                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "")
            }
        }

        IconButton(
            modifier = Modifier,
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnNextButtonClick)
            },
        ) {
            Icon(
                Icons.Rounded.SkipNext,
                contentDescription = "",
            )
        }

        IconButton(
            modifier = Modifier,
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnPlayModeButtonClick)
            },
        ) {
            Icon(
                imageVector = playMode.getIcon(),
                contentDescription = "",
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
private fun PlayInfoWithAlbumCover(
    modifier: Modifier,
    coverUri: String,
    title: String,
    artist: String
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleBorderImage(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .padding(6.dp),
            model = coverUri
        )

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = artist,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
