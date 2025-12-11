/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.shrinkable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.util.getIcon
import com.andannn.melodify.ui.widgets.SmpMainIconButton
import com.andannn.melodify.ui.widgets.SmpSubIconButton

@Composable
internal fun PlayControlButtons(
    isShuffle: Boolean,
    isPlaying: Boolean,
    playMode: PlayMode,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    onEvent: (PlayerUiEvent) -> Unit = {},
) {
    Row(
        modifier = modifier,
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
