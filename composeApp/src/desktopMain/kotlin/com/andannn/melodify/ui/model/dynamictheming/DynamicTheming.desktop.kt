/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:standard:filename")

package com.andannn.melodify.ui.model.dynamictheming

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun rememberDominantColorState(
    defaultColor: Color,
    defaultOnColor: Color,
    cacheSize: Int,
    isColorValid: (Color) -> Boolean,
): DominantColorState {
    error("")
}

actual class DominantColorState {
    actual val color: Color = Color.Red

    actual suspend fun updateColorsFromImageUrl(url: String) {
    }

    actual fun setDynamicThemeEnable(enable: Boolean) {
    }
}
