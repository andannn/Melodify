/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
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
    shape: Shape = MaterialTheme.shapes.extraSmall,
    contentScale: ContentScale = ContentScale.Fit,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
) {
    if (model == null) return

    Box(
        modifier =
            modifier
                .clip(shape = shape),
    ) {
        if (isIOSCustomMPLibraryUri(model)) {
            IOSMediaArtworkView(
                modifier = Modifier.fillMaxSize(),
                coverUri = model,
            )

            return
        }

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
