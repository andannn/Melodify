/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.default_image_icon
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
    actionType: ActionType = ActionType.OPTION,
    swapIconModifier: Modifier? = null,
    albumArtUri: String = "",
    isActive: Boolean = false,
    defaultColor: Color = MaterialTheme.colorScheme.surface,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    title: String = "",
    subTitle: String = "",
    trackNum: Int = 0,
    showTrackNum: Boolean = false,
    onMusicItemClick: (() -> Unit)? = null,
    onOptionButtonClick: (() -> Unit)? = null,
) {
    @Composable
    fun CustomContainer(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        if (onMusicItemClick != null) {
            Surface(
                modifier = modifier,
                onClick = onMusicItemClick,
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
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier) {
                if (showTrackNum) {
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
                        modifier =
                            Modifier
                                .size(50.dp)
                                .clip(MaterialTheme.shapes.extraSmall),
                        placeholder = painterResource(defaultImagePlaceholderRes),
                        error = painterResource(defaultImagePlaceholderRes),
                        contentScale = ContentScale.Crop,
                        model = albumArtUri,
                        contentDescription = "",
                    )
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
