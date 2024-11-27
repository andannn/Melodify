package com.andannn.melodify.feature.common.dynamic_theming

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.feature.common.theme.DarkColorPalette
import com.andannn.melodify.feature.common.theme.LightColorPalette

/**
 * Not support dynamic color in desktop app.
 */
actual fun createThemeFromSeed(
    seedColor: Color,
    isDark: Boolean,
    dynamicSchemeVariant: DynamicSchemeVariant,
    contrastLevel: Double,
    primary: Color?,
    onPrimary: Color?,
    primaryContainer: Color?,
    onPrimaryContainer: Color?,
    inversePrimary: Color?,
    secondary: Color?,
    onSecondary: Color?,
    secondaryContainer: Color?,
    onSecondaryContainer: Color?,
    tertiary: Color?,
    onTertiary: Color?,
    tertiaryContainer: Color?,
    onTertiaryContainer: Color?,
    background: Color?,
    onBackground: Color?,
    surface: Color?,
    onSurface: Color?,
    surfaceVariant: Color?,
    onSurfaceVariant: Color?,
    surfaceTint: Color?,
    inverseSurface: Color?,
    inverseOnSurface: Color?,
    error: Color?,
    onError: Color?,
    errorContainer: Color?,
    onErrorContainer: Color?,
    outline: Color?,
    outlineVariant: Color?,
    scrim: Color?,
    surfaceBright: Color?,
    surfaceDim: Color?,
    surfaceContainer: Color?,
    surfaceContainerHigh: Color?,
    surfaceContainerHighest: Color?,
    surfaceContainerLow: Color?,
    surfaceContainerLowest: Color?
): ColorScheme {
    return if (isDark) DarkColorPalette else LightColorPalette
}