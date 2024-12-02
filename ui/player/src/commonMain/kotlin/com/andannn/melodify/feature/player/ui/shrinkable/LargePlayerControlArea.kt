package com.andannn.melodify.ui.player.ui.shrinkable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.common.widgets.AutoResizedText
import com.andannn.melodify.ui.common.widgets.SmpMainIconButton
import com.andannn.melodify.ui.common.widgets.SmpSubIconButton
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.player.PlayerUiEvent
import com.andannn.melodify.ui.player.util.getIcon
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun LargePlayerControlArea(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    progress: Float = 0.5f,
    enable: Boolean = true,
    isPlaying: Boolean = false,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    onEvent: (PlayerUiEvent) -> Unit = {},
    isShuffle: Boolean = false,
) {
    val titleState by rememberUpdatedState(newValue = title)
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            AutoResizedText(
                modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
                text = titleState,
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium,
            )
            AutoResizedText(
                modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
                text = artist,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Slider(
            modifier = Modifier.padding(horizontal = 4.dp),
            value = progress,
            enabled = enable,
            onValueChange = {
                onEvent(PlayerUiEvent.OnProgressChange(it))
            },
        )
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmpSubIconButton(
                modifier =
                Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                imageVector = if (isShuffle) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle,
                enabled = enable,
                onClick = {
                    onEvent(PlayerUiEvent.OnShuffleButtonClick)
                },
            )
            SmpSubIconButton(
                modifier =
                Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                scale = 2f,
                enabled = enable,
                imageVector = Icons.Rounded.SkipPrevious,
                onClick = {
                    onEvent(PlayerUiEvent.OnPreviousButtonClick)
                },
            )

            SmpMainIconButton(
                modifier =
                Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                enabled = enable,
                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                onClick = {
                    onEvent(PlayerUiEvent.OnPlayButtonClick)
                },
            )
            SmpSubIconButton(
                modifier =
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(10.dp),
                scale = 2f,
                enabled = enable,
                imageVector = Icons.Rounded.SkipNext,
                onClick = {
                    onEvent(PlayerUiEvent.OnNextButtonClick)
                },
            )
            SmpSubIconButton(
                modifier = Modifier.weight(1f),
                imageVector = playMode.getIcon(),
                enabled = enable,
                onClick = {
                    onEvent(PlayerUiEvent.OnPlayModeButtonClick)
                },
            )
        }
    }
}

@Preview
@Composable
private fun LargeControlAreaPreview() {
    MelodifyTheme(darkTheme = false) {
        Surface {
            LargePlayerControlArea(
                modifier = Modifier.width(530.dp).height(250.dp),
                title = "title",
                artist = "artist",
            )
        }
    }
}
