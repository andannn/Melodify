/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun rememberDominantColorState(
    defaultColor: Color,
    defaultOnColor: Color,
    cacheSize: Int,
    isColorValid: (Color) -> Boolean,
): DominantColorState =
    object : DominantColorState {
        override val color: Color
            get() = defaultColor

        override suspend fun updateColorsFromImageUrl(url: String) {
        }

        override fun setDynamicThemeEnable(enable: Boolean) {
        }
    }

@Composable
actual fun DynamicThemePrimaryColorsFromImage(
    dominantColorState: DominantColorState,
    content: @Composable (() -> Unit),
) {
}
