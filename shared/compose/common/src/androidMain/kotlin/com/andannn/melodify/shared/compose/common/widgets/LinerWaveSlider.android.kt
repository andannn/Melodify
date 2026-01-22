/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
actual fun LinerWaveSlider(
    value: Float,
    modifier: Modifier,
    onValueChange: (Float) -> Unit,
    onStartDrag: () -> Unit,
    onEndDrag: () -> Unit,
    playing: Boolean,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val isDraggedState = interactionSource.collectIsDraggedAsState()
    LaunchedEffect(Unit) {
        snapshotFlow { isDraggedState.value }.drop(1).collect { isDragged ->
            if (isDragged) {
                onStartDrag()
            } else {
                onEndDrag()
            }
        }
    }

    Slider(
        modifier = modifier,
        interactionSource = interactionSource,
        value = value,
        onValueChange = onValueChange,
        track = { sliderState ->
            LinearWavyProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { sliderState.value },
                stroke =
                    Stroke(
                        width =
                            with(LocalDensity.current) {
                                3.dp.toPx()
                            },
                        cap = StrokeCap.Round,
                    ),
                wavelength = 60.dp,
                waveSpeed = 30.dp,
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
