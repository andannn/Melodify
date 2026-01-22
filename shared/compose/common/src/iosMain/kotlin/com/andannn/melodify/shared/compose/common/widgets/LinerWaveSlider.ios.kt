/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.material3.Slider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@androidx.compose.runtime.Composable
actual fun LinerWaveSlider(
    value: Float,
    modifier: Modifier,
    onValueChange: (Float) -> Unit,
    onStartDrag: () -> Unit,
    onEndDrag: () -> Unit,
    playing: Boolean,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    LaunchedEffect(isDragged) {
        if (isDragged) {
            onStartDrag()
        } else {
            onStartDrag()
        }
    }

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}
