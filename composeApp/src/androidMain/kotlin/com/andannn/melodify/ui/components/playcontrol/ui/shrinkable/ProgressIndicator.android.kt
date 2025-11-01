/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun ProgressIndicator(
    progress: Float,
    modifier: Modifier,
    playing: Boolean,
) {
    LinearWavyProgressIndicator(
        progress = { progress },
        modifier = modifier,
        stroke =
            Stroke(
                width =
                    with(LocalDensity.current) {
                        3.dp.toPx()
                    },
                cap = StrokeCap.Round,
            ),
        gapSize = 1.dp,
        wavelength = 60.dp,
        waveSpeed = 20.dp,
        amplitude = { progress ->
            if (!playing || progress <= 0.1f || progress >= 0.95f) {
                0f
            } else {
                1f
            }
        },
    )
}
