/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme

@Composable
internal fun GroupIndicator(
    modifier: Modifier,
    isLast: Boolean,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Spacer(
        modifier =
            modifier.fillMaxHeight().drawBehind {
                val strokeWidth = 1.dp.toPx()
                val startX = size.width.div(2f)
                val startY = 0f
                val endX = startX
                val endY = if (isLast) size.height.div(2) - size.width.div(2f) else size.height

                drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth)

                val arcTopLeftY = size.height.div(2) - size.width
                drawArc(
                    color = color,
                    topLeft = Offset(startX, arcTopLeftY),
                    size = Size(size.width, size.width),
                    useCenter = false,
                    startAngle = 180f,
                    sweepAngle = -90f,
                    style = Stroke(width = strokeWidth),
                )
            },
    )
}

@Composable
internal fun GroupConnection(
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Spacer(
        modifier =
            modifier.fillMaxHeight().drawBehind {
                val strokeWidth = 1.dp.toPx()
                val startX = size.width.div(2f)
                val startY = 0f
                val endX = startX
                val endY = size.height
                drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth)
            },
    )
}

@Preview
@Composable
private fun GroupIndicatorPreview() {
    MelodifyTheme {
        Surface {
            GroupIndicator(modifier = Modifier.size(150.dp), isLast = true)
        }
    }
}

@Preview
@Composable
private fun GroupIndicatorPreview2() {
    MelodifyTheme {
        Surface {
            GroupIndicator(modifier = Modifier.size(150.dp), isLast = false)
        }
    }
}

@Preview
@Composable
private fun GroupConnectionPreview2() {
    MelodifyTheme {
        Surface {
            GroupConnection(modifier = Modifier.size(150.dp))
        }
    }
}
