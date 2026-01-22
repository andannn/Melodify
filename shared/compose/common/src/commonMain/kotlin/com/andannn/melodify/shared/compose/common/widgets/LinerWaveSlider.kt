/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun LinerWaveSlider(
    value: Float,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    playing: Boolean = true,
)
