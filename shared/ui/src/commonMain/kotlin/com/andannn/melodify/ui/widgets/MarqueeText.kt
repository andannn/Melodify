/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.theme.MelodifyTheme
import kotlinx.coroutines.delay

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    speedPxPerSec: Float = 60f,
    spacingBetweenCopies: Dp = 10.dp,
    loopDelayMillis: Long = 4000,
    style: TextStyle = LocalTextStyle.current,
) {
    var containerWidth by remember { mutableStateOf(0f) }

    val offsetX = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()
    val result =
        remember(text, style) {
            textMeasurer.measure(
                text = text,
                style = style,
            )
        }

    val textWidth = result.size.width

    val shouldScroll =
        remember(containerWidth, textWidth) {
            textWidth > containerWidth && containerWidth > 0f
        }

    val density = LocalDensity.current

    LaunchedEffect(shouldScroll, textWidth, containerWidth) {
        if (!shouldScroll) {
            offsetX.snapTo(0f)
            return@LaunchedEffect
        }

        val gapPx = with(density) { spacingBetweenCopies.toPx() }
        val loopChunk = textWidth + gapPx

        while (true) {
            offsetX.snapTo(0f)

            val durationMillis = ((loopChunk / speedPxPerSec) * 1000f).toInt()

            delay(loopDelayMillis)
            offsetX.animateTo(
                targetValue = -loopChunk,
                animationSpec =
                    tween(
                        durationMillis = durationMillis,
                        easing = LinearEasing,
                    ),
            )
        }
    }

    Box(
        modifier =
            modifier
                .clipToBounds()
                .onSizeChanged {
                    containerWidth = it.width.toFloat()
                },
    ) {
        if (!shouldScroll) {
            Text(
                text = text,
                style = style,
                maxLines = 1,
                softWrap = false,
            )
        } else {
            val gapDp = spacingBetweenCopies
            val currentOffsetX = offsetX.value
            MarqueeViewport(
                contentOffsetPx = currentOffsetX,
            ) {
                Text(
                    text = text,
                    style = style,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Visible,
                )

                Spacer(modifier = Modifier.width(gapDp))

                Text(
                    text = text,
                    style = style,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Visible,
                )
            }
        }
    }
}

@Composable
private fun MarqueeViewport(
    modifier: Modifier = Modifier,
    contentOffsetPx: Float,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .clipToBounds(),
    ) {
        Layout(
            content = content,
            modifier =
                Modifier
                    .offset { IntOffset(contentOffsetPx.toInt(), 0) },
        ) { measurables, constraints ->
            val looseConstraints =
                constraints.copy(
                    minWidth = 0,
                    maxWidth = Constraints.Infinity,
                )

            val placeables = measurables.map { it.measure(looseConstraints) }

            val contentWidth = placeables.maxOf { it.width }
            val contentHeight = placeables.maxOf { it.height }

            val layoutWidth = constraints.maxWidth
            val layoutHeight =
                contentHeight.coerceIn(
                    constraints.minHeight,
                    constraints.maxHeight,
                )

            layout(layoutWidth, layoutHeight) {
                var offsetX = 0
                placeables.forEach { p ->
                    p.placeRelative(offsetX, 0)
                    offsetX += p.width
                }
            }
        }
    }
}

@Preview
@Composable
private fun MarqueeTextPreview() {
    MelodifyTheme(content = {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            MarqueeText(
                modifier = Modifier.width(120.dp).align(Alignment.Center).background(Color.Red),
                text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            )
        }
    })
}
