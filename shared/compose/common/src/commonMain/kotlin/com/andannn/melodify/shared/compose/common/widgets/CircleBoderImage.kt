/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CircleBorderImage(
    model: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    contentScale: ContentScale = ContentScale.Fit,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
) {
    if (model == null) return

    Surface(
        modifier = modifier,
        shape = shape,
    ) {
        if (isIOSCustomMPLibraryUri(model)) {
            IOSMediaArtworkView(
                modifier = Modifier.fillMaxSize(),
                coverUri = model,
            )
        } else {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(model)
                        .size(Int.MAX_VALUE)
                        .build(),
                contentScale = contentScale,
                placeholder = painterResource(defaultImagePlaceholderRes),
                error = painterResource(defaultImagePlaceholderRes),
                contentDescription = "",
            )
        }
    }
}
