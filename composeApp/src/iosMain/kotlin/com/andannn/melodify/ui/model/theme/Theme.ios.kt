/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.model.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.andannn.melodify.ui.theme.DarkColorPalette
import com.andannn.melodify.ui.theme.LightColorPalette

@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    isDynamicColor: Boolean,
): ColorScheme = if (darkTheme) DarkColorPalette else LightColorPalette
