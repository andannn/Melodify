/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.dynamictheming

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

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
fun DynamicThemePrimaryColorsFromImage(
    dominantColorStateImpl: DominantColorState = rememberDominantColorState(),
    content: @Composable () -> Unit,
) {
    val defaultScheme = MaterialTheme.colorScheme
    var scheme: ColorScheme by remember { mutableStateOf(defaultScheme) }
    val seedColor by
        animateColorAsState(
            dominantColorStateImpl.color,
            spring(stiffness = Spring.StiffnessLow),
            finishedListener = {
                scheme = createThemeFromSeed(it, isDark = true)
            },
            label = "domain color",
        )

    var debounceCounter by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(seedColor) {
        debounceCounter += 1
        if (debounceCounter % 5 == 0) {
            scheme = createThemeFromSeed(seedColor, isDark = true)
        }
    }

    MaterialTheme(
        colorScheme = scheme,
        content = content,
    )
}

expect class DominantColorState {
    val color: Color

    suspend fun updateColorsFromImageUrl(url: String)

    fun setDynamicThemeEnable(enable: Boolean)
}
