/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.util.brightness.BrightnessState

@Composable
internal fun BrightnessIndicator(
    state: BrightnessState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(state.icon(), contentDescription = null)

        val progress =
            remember(state) {
                when (state) {
                    BrightnessState.Auto -> 0f
                    is BrightnessState.Manual -> state.brightness
                }
            }
        Spacer(Modifier.width(4.dp))
        LinearProgressIndicator(
            modifier = Modifier.width(120.dp),
            progress = { progress },
            drawStopIndicator = {},
        )

        val text =
            remember(state) {
                when (state) {
                    BrightnessState.Auto -> "Auto"
                    is BrightnessState.Manual -> "${(progress * 100).toInt()}%"
                }
            }
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun BrightnessState.icon() =
    when (this) {
        BrightnessState.Auto -> {
            Icons.Default.BrightnessAuto
        }

        is BrightnessState.Manual -> {
            when {
                brightness < 0.33f -> {
                    Icons.Default.BrightnessLow
                }

                brightness < 0.66f -> {
                    Icons.Default.BrightnessMedium
                }

                brightness < 1f -> {
                    Icons.Default.BrightnessHigh
                }

                else -> {
                    Icons.Default.BrightnessHigh
                }
            }
        }
    }

@Preview
@Composable
private fun BrightnessIndicatorPreview() {
    MelodifyTheme {
        Surface {
            BrightnessIndicator(state = BrightnessState.Auto)
        }
    }
}

@Preview
@Composable
private fun BrightnessIndicatorPreview2() {
    MelodifyTheme {
        Surface {
            BrightnessIndicator(state = BrightnessState.Manual(0.6f))
        }
    }
}
