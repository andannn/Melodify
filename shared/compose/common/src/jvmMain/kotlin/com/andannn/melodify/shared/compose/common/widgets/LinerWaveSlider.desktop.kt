/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun LinerWaveSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    playing: Boolean,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}
