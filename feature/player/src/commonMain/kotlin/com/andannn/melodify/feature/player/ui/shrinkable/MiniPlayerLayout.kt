package com.andannn.melodify.feature.player.ui.shrinkable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.feature.common.theme.MelodifyTheme
import com.andannn.melodify.feature.player.PlayerUiEvent
import com.andannn.melodify.feature.player.ui.ShrinkPlayerHeight
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MiniPlayerLayout(
    title: String,
    artist: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    val titleState = rememberUpdatedState(title)
    val artistState = rememberUpdatedState(artist)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = titleState.value,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = artistState.value,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(
            modifier =
            Modifier
                .size(30.dp)
                .scale(1.2f),
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
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            modifier =
            Modifier
                .size(30.dp)
                .scale(1.2f)
                .rotate(180f),
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
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            modifier = Modifier.size(30.dp),
            enabled = enabled,
            onClick = {
                onEvent(PlayerUiEvent.OnFavoriteButtonClick)
            },
        ) {
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    tint = Color.Red,
                    contentDescription = "",
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = "",
                )
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
    }
}

@Preview
@Composable
private fun MiniPlayerLayoutPreview() {
    MelodifyTheme {
        Surface {
            MiniPlayerLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShrinkPlayerHeight),
                title = "BBBBB",
                artist = "AAAAA",
                isPlaying = true,
                isFavorite = false,
                enabled = true,
            )
        }
    }
}
