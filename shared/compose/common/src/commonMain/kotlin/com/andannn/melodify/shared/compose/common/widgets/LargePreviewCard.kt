/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LargePreviewCard(
    artCoverUri: String,
    title: String,
    backGroundColor: Color = MaterialTheme.colorScheme.surface,
    subTitle: String? = null,
    modifier: Modifier = Modifier,
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
            MediaCoverImageWidget(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                model = artCoverUri,
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
    MelodifyTheme(
        content = {
            Surface {
                LargePreviewCard(
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
        },
    )
}
