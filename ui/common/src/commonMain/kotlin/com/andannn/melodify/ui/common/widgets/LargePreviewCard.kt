/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LargePreviewCard(
    artCoverUri: String,
    title: String,
    backGroundColor: Color = MaterialTheme.colorScheme.surface,
    subTitle: String? = null,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    Surface(
        modifier =
            if (onClick == null) {
                modifier
            } else {
                modifier.combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
            },
        color = backGroundColor,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column {
            AsyncImage(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .then(imageModifier),
                placeholder = painterResource(defaultImagePlaceholderRes),
                contentScale = ContentScale.Crop,
                error = painterResource(defaultImagePlaceholderRes),
                model = artCoverUri,
                contentDescription = "",
            )

            Spacer(modifier = Modifier.height(5.dp))

            Column(modifier = Modifier.padding(vertical = 3.dp, horizontal = 5.dp)) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (subTitle != null) {
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AlbumCardPreview() {
    MelodifyTheme {
        Surface {
            LargePreviewCard(
                imageModifier =
                    Modifier
                        .clip(shape = CircleShape)
                        .alpha(0.3f)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)),
                artCoverUri = "",
                title =
                    "TitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitltleTitleTitleT" +
                        "itleTitleTitltleTitleTitleTitleTitleTitltleTitleTitleTitleTitleTitle" +
                        "TitleTitleTitleTitleTitleTitleTitleTitleTitleTitle",
                subTitle =
                    "Sub title Sub title Sub title Sub title Sub title Sub title Sub title" +
                        " Sub title Sub title Sub title Sub title Sub title Sub title Sub title" +
                        " Sub title Sub title ",
            )
        }
    }
}
