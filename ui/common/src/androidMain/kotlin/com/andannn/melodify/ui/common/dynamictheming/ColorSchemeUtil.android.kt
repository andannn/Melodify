package com.andannn.melodify.ui.common.dynamictheming

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
    surfaceContainerLowest: Color?,
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

fun buildDynamicScheme(
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
