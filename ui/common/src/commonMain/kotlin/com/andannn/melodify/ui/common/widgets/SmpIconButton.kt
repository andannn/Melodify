/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SmpMainIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    TextButton(
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(),
        onClick = onClick,
    ) {
        Icon(modifier = Modifier.scale(1.5f), imageVector = imageVector, contentDescription = null)
    }
}

@Composable
fun SmpSubIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    scale: Float = 1.0f,
    onClick: () -> Unit = {},
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.scale(scale),
            imageVector = imageVector,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun SmpMainIconButtonPreview() {
    MaterialTheme {
        SmpMainIconButton(
            onClick = {},
            imageVector = Icons.Rounded.PlayArrow,
        )
    }
}

@Preview
@Composable
private fun SmpSubIconButtonPreview() {
    MaterialTheme {
        SmpSubIconButton(
            onClick = {},
            imageVector = Icons.Rounded.PlayArrow,
            enabled = true,
        )
    }
}
