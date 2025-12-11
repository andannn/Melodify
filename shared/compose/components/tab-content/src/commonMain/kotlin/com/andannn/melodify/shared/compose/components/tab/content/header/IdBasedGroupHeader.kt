/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.andannn.melodify.shared.compose.common.Presenter
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun GroupHeader(
    isPrimary: Boolean,
    groupInfo: GroupInfo,
    modifier: Modifier = Modifier,
    presenter: Presenter<GroupHeaderState> = retainGroupHeaderPresenter(groupInfo),
    onGroupHeaderClick: () -> Unit = {},
) {
    val state = presenter.present()
    HeaderInfo(
        modifier = modifier,
        isPrimary = isPrimary,
        coverArtUri = state.cover,
        title = state.title,
        onOptionClick = {
            state.eventSink.invoke(GroupHeaderEvent.OnOptionClick)
        },
        onClick = onGroupHeaderClick,
    )
}

@Composable
private fun HeaderInfo(
    modifier: Modifier = Modifier,
    coverArtUri: String?,
    isPrimary: Boolean,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    title: String = "",
    onOptionClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                    .height(IntrinsicSize.Min),
        ) {
            if (coverArtUri != null) {
                AsyncImage(
                    modifier =
                        Modifier
                            .align(Alignment.CenterVertically)
                            .size(48.dp)
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
                    val style =
                        if (isPrimary) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.titleMedium
                        }
                    Text(
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = style.copy(fontWeight = FontWeight.Bold),
                    )

                    IconButton(
                        modifier = Modifier.align(Alignment.Bottom),
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
