/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.domain.model.PlayerState
import com.andannn.melodify.shared.compose.common.getIcon
import com.andannn.melodify.shared.compose.common.widgets.PlayButtonContent
import com.andannn.melodify.shared.compose.common.widgets.SmpSubIconButton
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent

@Composable
internal fun PlayControlButtons(
    isShuffle: Boolean,
    playerState: PlayerState,
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

        TextButton(
            modifier =
                Modifier
                    .weight(1f)
                    .aspectRatio(1f),
            enabled = enable,
            colors = ButtonDefaults.buttonColors(),
            onClick = {
                onEvent(PlayerUiEvent.OnPlayButtonClick)
            },
        ) {
            PlayButtonContent(playerState)
        }

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
