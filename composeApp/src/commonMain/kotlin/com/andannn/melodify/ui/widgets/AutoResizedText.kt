/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isUnspecified

@Composable
fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = style.color,
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = MaterialTheme.typography.bodyLarge.fontSize

    Text(
        text = text,
        color = color,
        modifier =
            modifier.drawWithContent {
                if (shouldDraw) {
                    drawContent()
                }
            },
        maxLines = maxLines,
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (style.fontSize.isUnspecified) {
                    resizedTextStyle =
                        resizedTextStyle.copy(
                            fontSize = defaultFontSize,
                        )
                }
                resizedTextStyle =
                    resizedTextStyle.copy(
                        fontSize = resizedTextStyle.fontSize * 0.95,
                    )
            } else {
                shouldDraw = true
            }
        },
    )
}
