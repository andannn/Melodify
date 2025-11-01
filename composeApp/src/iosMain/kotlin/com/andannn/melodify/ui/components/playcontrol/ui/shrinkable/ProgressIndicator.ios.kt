/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
actual fun ProgressIndicator(
    progress: Float,
    modifier: Modifier,
    playing: Boolean,
) {
    Spacer(
        modifier =
            modifier
                .fillMaxWidth(fraction = progress)
                .height(3.dp)
                .background(
                    brush =
                        Brush.horizontalGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.tertiaryContainer,
                                    MaterialTheme.colorScheme.inversePrimary,
                                    MaterialTheme.colorScheme.primary,
                                ),
                        ),
                ),
    )
}
