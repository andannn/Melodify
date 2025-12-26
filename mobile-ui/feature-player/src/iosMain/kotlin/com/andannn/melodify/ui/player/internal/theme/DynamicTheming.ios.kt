/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme

@Composable
actual fun DynamicThemePrimaryColorsFromImage(
    dominantColorState: DominantColorState,
    isDarkTheme: Boolean,
    content: @Composable (() -> Unit),
) {
    MelodifyTheme(darkTheme = isDarkTheme) {
        content()
    }
}

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
