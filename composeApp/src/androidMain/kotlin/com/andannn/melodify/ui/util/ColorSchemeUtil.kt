/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.util

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeContent
import com.materialkolor.scheme.SchemeExpressive
import com.materialkolor.scheme.SchemeFidelity
import com.materialkolor.scheme.SchemeFruitSalad
import com.materialkolor.scheme.SchemeMonochrome
import com.materialkolor.scheme.SchemeNeutral
import com.materialkolor.scheme.SchemeRainbow
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant

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

fun createThemeFromSeed(
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
): ColorScheme {
    val scheme: DynamicScheme =
        buildDynamicScheme(isDark, seedColor, dynamicSchemeVariant, contrastLevel)

    return ColorScheme(
        primary = primary ?: Color(MaterialDynamicColors().primary().getArgb(scheme)),
        onPrimary = onPrimary ?: Color(MaterialDynamicColors().onPrimary().getArgb(scheme)),
        primaryContainer = primaryContainer ?: Color(MaterialDynamicColors().primaryContainer().getArgb(scheme)),
        onPrimaryContainer = onPrimaryContainer ?: Color(MaterialDynamicColors().onPrimaryContainer().getArgb(scheme)),
        inversePrimary = inversePrimary ?: Color(MaterialDynamicColors().inversePrimary().getArgb(scheme)),
        secondary = secondary ?: Color(MaterialDynamicColors().secondary().getArgb(scheme)),
        onSecondary = onSecondary ?: Color(MaterialDynamicColors().onSecondary().getArgb(scheme)),
        secondaryContainer = secondaryContainer ?: Color(MaterialDynamicColors().secondaryContainer().getArgb(scheme)),
        onSecondaryContainer = onSecondaryContainer ?: Color(MaterialDynamicColors().onSecondaryContainer().getArgb(scheme)),
        tertiary = tertiary ?: Color(MaterialDynamicColors().tertiary().getArgb(scheme)),
        onTertiary = onTertiary ?: Color(MaterialDynamicColors().onTertiary().getArgb(scheme)),
        tertiaryContainer = tertiaryContainer ?: Color(MaterialDynamicColors().tertiaryContainer().getArgb(scheme)),
        onTertiaryContainer = onTertiaryContainer ?: Color(MaterialDynamicColors().onTertiaryContainer().getArgb(scheme)),
        background = background ?: Color(MaterialDynamicColors().background().getArgb(scheme)),
        onBackground = onBackground ?: Color(MaterialDynamicColors().onBackground().getArgb(scheme)),
        surface = surface ?: Color(MaterialDynamicColors().surface().getArgb(scheme)),
        onSurface = onSurface ?: Color(MaterialDynamicColors().onSurface().getArgb(scheme)),
        surfaceVariant = surfaceVariant ?: Color(MaterialDynamicColors().surfaceVariant().getArgb(scheme)),
        onSurfaceVariant = onSurfaceVariant ?: Color(MaterialDynamicColors().onSurfaceVariant().getArgb(scheme)),
        surfaceTint = surfaceTint ?: Color(MaterialDynamicColors().surfaceTint().getArgb(scheme)),
        inverseSurface = inverseSurface ?: Color(MaterialDynamicColors().inverseSurface().getArgb(scheme)),
        inverseOnSurface = inverseOnSurface ?: Color(MaterialDynamicColors().inverseOnSurface().getArgb(scheme)),
        error = error ?: Color(MaterialDynamicColors().error().getArgb(scheme)),
        onError = onError ?: Color(MaterialDynamicColors().onError().getArgb(scheme)),
        errorContainer = errorContainer ?: Color(MaterialDynamicColors().errorContainer().getArgb(scheme)),
        onErrorContainer = onErrorContainer ?: Color(MaterialDynamicColors().onErrorContainer().getArgb(scheme)),
        outline = outline ?: Color(MaterialDynamicColors().outline().getArgb(scheme)),
        outlineVariant = outlineVariant ?: Color(MaterialDynamicColors().outlineVariant().getArgb(scheme)),
        scrim = scrim ?: Color(MaterialDynamicColors().scrim().getArgb(scheme)),
        surfaceBright = surfaceBright ?: Color(MaterialDynamicColors().surfaceBright().getArgb(scheme)),
        surfaceDim = surfaceDim ?: Color(MaterialDynamicColors().surfaceDim().getArgb(scheme)),
        surfaceContainer = surfaceContainer ?: Color(MaterialDynamicColors().surfaceContainer().getArgb(scheme)),
        surfaceContainerHigh = surfaceContainerHigh ?: Color(MaterialDynamicColors().surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = surfaceContainerHighest ?: Color(MaterialDynamicColors().surfaceContainerHighest().getArgb(scheme)),
        surfaceContainerLow = surfaceContainerLow ?: Color(MaterialDynamicColors().surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = surfaceContainerLowest ?: Color(MaterialDynamicColors().surfaceContainerLowest().getArgb(scheme)),
    )
}

private fun buildDynamicScheme(
    isDark: Boolean,
    seedColor: Color,
    schemeVariant: DynamicSchemeVariant,
    contrastLevel: Double,
): DynamicScheme {
    val sourceColorHct: Hct = Hct.fromInt(seedColor.toArgb())
    return when (schemeVariant) {
        DynamicSchemeVariant.TONAL_SPOT ->
            SchemeTonalSpot(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.FIDELITY ->
            SchemeFidelity(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.CONTENT ->
            SchemeContent(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.MONOCHROME ->
            SchemeMonochrome(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.NEUTRAL ->
            SchemeNeutral(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.VIBRANT ->
            SchemeVibrant(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.EXPRESSIVE ->
            SchemeExpressive(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.RAINBOW ->
            SchemeRainbow(
                sourceColorHct,
                isDark,
                contrastLevel,
            )

        DynamicSchemeVariant.FRUIT_SALAD ->
            SchemeFruitSalad(
                sourceColorHct,
                isDark,
                contrastLevel,
            )
    }
}
