/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Desktop theme.
 * Not support dynamic color theme.
 */
@Composable
actual fun getColorScheme(
    darkTheme: Boolean,
    isDynamicColor: Boolean,
): ColorScheme = if (darkTheme) DarkColorPalette else LightColorPalette
