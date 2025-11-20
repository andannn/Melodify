/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class ActionType {
    NONE,
    OPTION,
    SWAP,
}

@Composable
fun ListTileItemView(
    modifier: Modifier = Modifier,
    playable: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
    actionType: ActionType = ActionType.OPTION,
    swapIconModifier: Modifier? = null,
    thumbnailSourceUri: String? = null,
    isActive: Boolean = false,
    defaultColor: Color = MaterialTheme.colorScheme.surface,
    errorPlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    title: String = "",
    subTitle: String = "",
    trackNum: Int? = null,
    onItemClick: (() -> Unit)? = null,
    onOptionButtonClick: (() -> Unit)? = null,
) {
    @Composable
    fun CustomContainer(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        if (onItemClick != null) {
            Surface(
                modifier = modifier,
                onClick = onItemClick,
                color = if (isActive) MaterialTheme.colorScheme.inversePrimary else defaultColor,
                content = content,
            )
        } else {
            Surface(
                modifier = modifier,
                content = content,
                color = if (isActive) MaterialTheme.colorScheme.inversePrimary else defaultColor,
            )
        }
    }

    CustomContainer(
        modifier =
            modifier
                .fillMaxWidth()
                .alpha(if (playable) 1f else 0.5f),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (trackNum != null || thumbnailSourceUri != null) {
                Box {
                    if (trackNum != null) {
                        Text(
                            modifier =
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.extraSmall,
                                    ).align(Alignment.Center)
                                    .width(30.dp),
                            text = trackNum.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        AsyncImage(
                            model =
                                ImageRequest
                                    .Builder(LocalPlatformContext.current)
                                    .data(thumbnailSourceUri)
                                    .size(256)
                                    .build(),
                            modifier =
                                Modifier
                                    .size(50.dp)
                                    .background(MaterialTheme.colorScheme.surfaceDim)
                                    .clip(MaterialTheme.shapes.extraSmall),
                            error = painterResource(errorPlaceholderRes),
                            contentScale = ContentScale.Crop,
                            contentDescription = "",
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subTitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            when (actionType) {
                ActionType.NONE -> Spacer(Modifier)
                ActionType.OPTION ->
                    IconButton(
                        enabled = playable,
                        onClick = onOptionButtonClick!!,
                    ) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "menu")
                    }

                ActionType.SWAP ->
                    Icon(
                        modifier = Modifier.padding(12.dp).then(swapIconModifier!!),
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "swap",
                    )
            }
        }
    }
}
