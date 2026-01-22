/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.PlayerState
import com.andannn.melodify.shared.compose.common.widgets.FavoriteIconButton
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent

@Composable
internal fun MiniPlayerLayout(
    title: String,
    subTitle: String,
    playerState: PlayerState,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    val titleState = rememberUpdatedState(title)
    val artistState = rememberUpdatedState(subTitle)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayingInfo(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            title = titleState.value,
            artist = artistState.value,
        )
        IconButton(
            modifier =
                Modifier
                    .size(30.dp)
                    .scale(1.2f),
            onClick = {
                onEvent(PlayerUiEvent.OnPlayButtonClick)
            },
        ) {
            PlayButtonContent(playerState)
        }
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            modifier =
                Modifier
                    .size(30.dp)
                    .scale(1.2f)
                    .rotate(180f),
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
        FavoriteIconButton(
            modifier = Modifier.size(30.dp),
            isFavorite = isFavorite,
            onClick = {
                onEvent(PlayerUiEvent.OnFavoriteButtonClick)
            },
        )
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
private fun PlayingInfo(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
) {
    Column(
        modifier = modifier,
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
