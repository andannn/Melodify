/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.foundation.shape.RoundedCornerShape
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
                .clip(shape = RoundedCornerShape(8.dp)),
        model =
            ImageRequest
                .Builder(LocalPlatformContext.current)
                .data(model)
                .size(Int.MAX_VALUE)
                .build(),
        contentScale = ContentScale.Fit,
        contentDescription = "",
    )
}
