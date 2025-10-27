/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

interface DominantColorState {
    val color: Color

    suspend fun updateColorsFromImageUrl(url: String)

    fun setDynamicThemeEnable(enable: Boolean)
}

@Composable
expect fun rememberDominantColorState(
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    defaultOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    cacheSize: Int = 12,
    isColorValid: (Color) -> Boolean = { true },
): DominantColorState

/**
 * A composable which allows dynamic theming of the primary
 * color from an image.
 */
@Composable
expect fun DynamicThemePrimaryColorsFromImage(
    dominantColorState: DominantColorState,
    content: @Composable () -> Unit,
)
