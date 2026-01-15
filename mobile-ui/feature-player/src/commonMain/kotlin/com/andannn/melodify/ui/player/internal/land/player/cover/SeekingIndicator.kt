/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player.cover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

@Composable
internal fun SeekingIndicator(
    text: String?,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
) {
    val scaleAnim = remember { Animatable(0.5f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(text) {
        // Show
        scaleAnim.snapTo(0.5f)
        alphaAnim.snapTo(0.5f)

        val deffer1 =
            async {
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                )
            }
        val deffer2 =
            async {
                alphaAnim.animateTo(1f, tween(200))
            }

        deffer1.await()
        deffer2.await()

        delay(1000)

        // Hide
        async {
            scaleAnim.animateTo(0.5f)
        }
        async {
            alphaAnim.animateTo(0f)
        }
    }

    if (text != null) {
        Box(modifier = modifier) {
            Row(
                modifier =
                    Modifier
                        .graphicsLayer {
                            alpha = alphaAnim.value
                            scaleX = scaleAnim.value
                            scaleY = scaleAnim.value
                        }.clip(RoundedCornerShape(3.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isPositive) Icons.Default.SkipNext else Icons.Default.SkipPrevious,
                    tint = Color.White,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SeekingIndicatorPreview() {
    MelodifyTheme {
        SeekingIndicator(
            text = "10s",
            isPositive = true,
        )
    }
}
