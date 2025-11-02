/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
actual fun LinerWaveSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    playing: Boolean,
) {
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        track = { sliderState ->
            LinearWavyProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { sliderState.value },
                amplitude = { progress ->
                    if (!playing || progress <= 0.1f || progress >= 0.95f) {
                        0f
                    } else {
                        1f
                    }
                },
            )
        },
    )
}
