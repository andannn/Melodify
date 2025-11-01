/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun MelodifyTheme(
    darkTheme: Boolean,
    isDynamicColor: Boolean,
    content: @Composable (() -> Unit),
) {
    val colorScheme =
        if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
