/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun IdBasedGroupHeader(
    state: GroupHeaderState,
    modifier: Modifier = Modifier,
) {
    HeaderInfo(
        modifier = modifier,
        coverArtUri = state.cover,
        title = state.title,
        trackCount = state.trackCount,
        onOptionClick = {
            state.eventSink.invoke(GroupHeaderEvent.OnOptionClick)
        },
    )
}

@Composable
private fun HeaderInfo(
    modifier: Modifier = Modifier,
    coverArtUri: String?,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    title: String = "",
    trackCount: Int = 0,
    onOptionClick: () -> Unit = {},
) {
    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(5.dp)
                    .height(IntrinsicSize.Max),
        ) {
            if (coverArtUri != null) {
                AsyncImage(
                    modifier =
                        Modifier
                            .align(Alignment.CenterVertically)
                            .size(100.dp)
                            .clip(MaterialTheme.shapes.extraSmall),
                    model = coverArtUri,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(defaultImagePlaceholderRes),
                    error = painterResource(defaultImagePlaceholderRes),
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    Modifier.fillMaxHeight(),
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )

                    IconButton(
                        modifier = Modifier.padding(end = 6.dp).align(Alignment.Bottom),
                        onClick = onOptionClick,
                    ) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "menu")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
