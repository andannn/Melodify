/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.material3.Slider

@androidx.compose.runtime.Composable
actual fun LinerWaveSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: androidx.compose.ui.Modifier,
    playing: Boolean,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}
