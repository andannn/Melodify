/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:standard:filename")

package com.andannn.melodify.ui.player.internal.theme

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Scale
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface DominantColorState {
    val color: Color

    suspend fun updateColorsFromImageUrl(url: String)

    fun setDynamicThemeEnable(enable: Boolean)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DynamicThemePrimaryColorsFromImage(
    dominantColorState: DominantColorState,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable (() -> Unit),
) {
    val defaultScheme = MaterialTheme.colorScheme
    var scheme: ColorScheme by remember { mutableStateOf(defaultScheme) }
    val seedColor by
        animateColorAsState(
            dominantColorState.color,
            spring(stiffness = Spring.StiffnessLow),
            label = "domain color",
        )

    LaunchedEffect(seedColor, isDarkTheme) {
        scheme = createThemeFromSeed(seedColor, isDark = isDarkTheme)
    }

    MaterialExpressiveTheme(
        colorScheme = scheme,
        content = content,
    )
}

@Composable
fun rememberDominantColorState(
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    defaultOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    cacheSize: Int = 12,
    isColorValid: (Color) -> Boolean = { true },
): DominantColorState {
    val context = LocalContext.current
    return remember {
        DominantColorStateImpl(
            context,
            defaultColor,
            defaultOnColor,
            cacheSize,
            isColorValid,
        )
    }
}

/**
 * A class which stores and caches the result of any calculated dominant colors
 * from images.
 *
 * @param context Android context
 * @param defaultColor The default color, which will be used if [calculateDominantColor] fails to
 * calculate a dominant color
 * @param defaultOnColor The default foreground 'on color' for [defaultColor].
 * @param cacheSize The size of the [LruCache] used to store recent results. Pass `0` to
 * disable the cache.
 * @param isColorValid A lambda which allows filtering of the calculated image colors.
 */
@Stable
class DominantColorStateImpl(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    cacheSize: Int = 12,
    private val isColorValid: (Color) -> Boolean = { true },
) : DominantColorState {
    private val _color = mutableStateOf(defaultColor)

    override val color: Color
        get() = _color.value

    private var dynamicThemeEnable: Boolean = true

    private var colorFromImage: Color = defaultColor

    private val cache =
        when {
            cacheSize > 0 -> LruCache<String, DominantColors>(cacheSize)
            else -> null
        }

    override suspend fun updateColorsFromImageUrl(url: String) {
        val result = calculateDominantColor(url)
        colorFromImage = result?.color ?: defaultColor

        _color.value =
            if (dynamicThemeEnable) {
                colorFromImage
            } else {
                defaultColor
            }
    }

    private suspend fun calculateDominantColor(url: String): DominantColors? {
        val cached = cache?.get(url)
        if (cached != null) {
            // If we already have the result cached, return early now...
            return cached
        }

        // Otherwise we calculate the swatches in the image, and return the first valid color
        return calculateSwatchesInImage(context, url)
            // First we want to sort the list by the color's population
            .sortedByDescending { swatch -> swatch.population }
            // Then we want to find the first valid color
            .firstOrNull { swatch -> isColorValid(Color(swatch.rgb)) }
            // If we found a valid swatch, wrap it in a [DominantColors]
            ?.let { swatch ->
                DominantColors(
                    color = Color(swatch.rgb),
                    onColor = Color(swatch.bodyTextColor).copy(alpha = 1f),
                )
            }
            // Cache the resulting [DominantColors]
            ?.also { result -> cache?.put(url, result) }
    }

    override fun setDynamicThemeEnable(enable: Boolean) {
        dynamicThemeEnable = enable

        _color.value =
            if (dynamicThemeEnable) {
                colorFromImage
            } else {
                defaultColor
            }
    }
}

@Immutable
private data class DominantColors(
    val color: Color,
    val onColor: Color,
)

/**
 * Fetches the given [imageUrl] with Coil, then uses [Palette] to calculate the dominant color.
 */
private suspend fun calculateSwatchesInImage(
    context: Context,
    imageUrl: String,
): List<Palette.Swatch> {
    val request =
        ImageRequest
            .Builder(context)
            .data(imageUrl)
            // We scale the image to cover 128px x 128px (i.e. min dimension == 128px)
            .size(128)
            .scale(Scale.FILL)
            // Disable hardware bitmaps, since Palette uses Bitmap.getPixels()
            .allowHardware(false)
            // Set a custom memory cache key to avoid overwriting the displayed image in the cache
            .memoryCacheKey("$imageUrl.palette")
            .build()

    val bitmap =
        when (val result = context.imageLoader.execute(request)) {
            is SuccessResult -> result.image.toBitmap()
            else -> null
        }

    return bitmap?.let {
        withContext(Dispatchers.Default) {
            val palette =
                Palette
                    .Builder(bitmap)
                    // Disable any bitmap resizing in Palette. We've already loaded an appropriately
                    // sized bitmap through Coil
                    .resizeBitmapArea(0)
                    // Clear any built-in filters. We want the unfiltered dominant color
                    .clearFilters()
                    // We reduce the maximum color count down to 8
                    .maximumColorCount(8)
                    .generate()

            palette.swatches
        }
    } ?: emptyList()
}
