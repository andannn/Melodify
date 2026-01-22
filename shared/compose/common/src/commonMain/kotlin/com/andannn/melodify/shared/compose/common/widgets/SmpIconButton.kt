/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

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
import androidx.compose.ui.tooling.preview.Preview

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
private fun SmpSubIconButtonPreview() {
    MaterialTheme {
        SmpSubIconButton(
            onClick = {},
            imageVector = Icons.Rounded.PlayArrow,
            enabled = true,
        )
    }
}
