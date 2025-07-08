/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.dynamictheming

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

enum class DynamicSchemeVariant {
    // / Default for Material theme colors. Builds pastel palettes with a low chroma.
    TONAL_SPOT,

    // / The resulting color palettes match seed color, even if the seed color
    // / is very bright (high chroma).
    FIDELITY,

    // / All colors are grayscale, no chroma.
    MONOCHROME,

    // / Close to grayscale, a hint of chroma.
    NEUTRAL,

    // / Pastel colors, high chroma palettes. The primary palette's chroma is at
    // / maximum. Use `fidelity` instead if tokens should alter their tone to match
    // / the palette vibrancy.
    VIBRANT,

    // / Pastel colors, medium chroma palettes. The primary palette's hue is
    // / different from the seed color, for variety.
    EXPRESSIVE,

    // / Almost identical to `fidelity`. Tokens and palettes match the seed color.
    // / [ColorScheme.primaryContainer] is the seed color, adjusted to ensure
    // / contrast with surfaces. The tertiary palette is analogue of the seed color.
    CONTENT,

    // / A playful theme - the seed color's hue does not appear in the theme.
    RAINBOW,

    // / A playful theme - the seed color's hue does not appear in the theme.
    FRUIT_SALAD,
}

expect fun createThemeFromSeed(
    seedColor: Color,
    isDark: Boolean,
    dynamicSchemeVariant: DynamicSchemeVariant = DynamicSchemeVariant.TONAL_SPOT,
    contrastLevel: Double = 0.0,
    primary: Color? = null,
    onPrimary: Color? = null,
    primaryContainer: Color? = null,
    onPrimaryContainer: Color? = null,
    inversePrimary: Color? = null,
    secondary: Color? = null,
    onSecondary: Color? = null,
    secondaryContainer: Color? = null,
    onSecondaryContainer: Color? = null,
    tertiary: Color? = null,
    onTertiary: Color? = null,
    tertiaryContainer: Color? = null,
    onTertiaryContainer: Color? = null,
    background: Color? = null,
    onBackground: Color? = null,
    surface: Color? = null,
    onSurface: Color? = null,
    surfaceVariant: Color? = null,
    onSurfaceVariant: Color? = null,
    surfaceTint: Color? = null,
    inverseSurface: Color? = null,
    inverseOnSurface: Color? = null,
    error: Color? = null,
    onError: Color? = null,
    errorContainer: Color? = null,
    onErrorContainer: Color? = null,
    outline: Color? = null,
    outlineVariant: Color? = null,
    scrim: Color? = null,
    surfaceBright: Color? = null,
    surfaceDim: Color? = null,
    surfaceContainer: Color? = null,
    surfaceContainerHigh: Color? = null,
    surfaceContainerHighest: Color? = null,
    surfaceContainerLow: Color? = null,
    surfaceContainerLowest: Color? = null,
): ColorScheme
