/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme

@Composable
internal fun DoubleSpeedPlayLabel(modifier: Modifier = Modifier) {
    Text(
        modifier =
            modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 3.dp),
        text = "X2 speed",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
    )
}

@Preview
@Composable
private fun DoubleSpeedPlayLabelPreview() {
    MelodifyTheme {
        Surface {
            DoubleSpeedPlayLabel()
        }
    }
}
