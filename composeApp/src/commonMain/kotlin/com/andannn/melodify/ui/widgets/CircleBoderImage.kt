/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
fun CircleBorderImage(
    model: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier =
            modifier
                .clip(shape = CircleShape)
                .border(
                    shape = CircleShape,
                    border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary),
                ),
        model =
            ImageRequest
                .Builder(LocalPlatformContext.current)
                .data(model)
                .size(Int.MAX_VALUE)
                .build(),
        contentScale = ContentScale.Crop,
        contentDescription = "",
    )
}
