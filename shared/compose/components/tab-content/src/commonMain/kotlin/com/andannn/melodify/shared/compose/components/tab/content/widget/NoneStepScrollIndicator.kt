/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content.widget

import androidx.annotation.IntRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollIndicatorState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalSlider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.outline_arrow_range_24
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun NoneStepScrollIndicator(
    scrollIndicatorState: ScrollIndicatorState,
    modifier: Modifier = Modifier,
    onScrollBy: (Float) -> Unit = {},
) {
    val value by
        remember {
            derivedStateOf {
                val contentSize = scrollIndicatorState.contentSize
                val scrollOffset = scrollIndicatorState.scrollOffset
                if (contentSize == 0) {
                    0f
                } else {
                    scrollOffset
                        .toFloat()
                        .div(contentSize)
                        .coerceIn(0f, 1f)
                }
            }
        }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    var isVisible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(value) {
        isVisible = true
        delay(800)
        isVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        CustomVerticalScrollIndicator(
            value = value,
            interactionSource = interactionSource,
            onValueChange = { newValue ->
                if (isDragged) {
                    val contentSize = scrollIndicatorState.contentSize
                    onScrollBy((newValue - value).times(contentSize))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CustomVerticalScrollIndicator(
    value: Float,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @IntRange(from = 0) steps: Int = 0,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val state =
        remember(steps, valueRange) { SliderState(value, steps, onValueChangeFinished, valueRange) }
    state.onValueChangeFinished = onValueChangeFinished
    state.onValueChange = onValueChange
    state.value = value

    VerticalSlider(
        modifier = modifier,
        state = state,
        interactionSource = interactionSource,
        track = {
            Spacer(modifier.fillMaxHeight())
        },
        thumb = {
            val thumbTranslationX =
                with(LocalDensity.current) {
                    16.dp.toPx()
                }
            Surface(
                modifier =
                    Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            translationX = thumbTranslationX
                        },
                shadowElevation = 24.dp,
                color = MaterialTheme.colorScheme.secondaryFixed,
                shape = CircleShape,
            ) {
                Image(
                    modifier =
                        Modifier.graphicsLayer {
                            scaleX = 0.4f
                            scaleY = 0.4f
                            rotationZ = 90f
                            translationX = (-thumbTranslationX).div(3)
                        },
                    painter = painterResource(Res.drawable.outline_arrow_range_24),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryFixed),
                    contentDescription = null,
                )
            }
        },
    )
}
