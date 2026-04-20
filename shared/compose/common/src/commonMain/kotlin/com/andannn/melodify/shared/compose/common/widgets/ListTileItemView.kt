/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme

enum class ActionType {
    NONE,
    OPTION,
    SWAP,
}

@Composable
fun ListTileItemView(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
    actionType: ActionType = ActionType.OPTION,
    swapIconModifier: Modifier? = null,
    thumbnailSourceUri: String? = null,
    isSelected: Boolean = false,
    isActive: Boolean = false,
    defaultColor: Color = MaterialTheme.colorScheme.surface,
    title: String = "",
    subTitle: String = "",
    trackNum: Int? = null,
    onItemClick: (() -> Unit)? = null,
    onOptionButtonClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
) {
    @Composable
    fun CustomContainer(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        if (onItemClick != null) {
            val combinedClickModifier = Modifier.combinedClickable(onClick = onItemClick, onLongClick = onLongPress)
            Surface(
                modifier = modifier.then(combinedClickModifier),
                color = if (isActive || isSelected) MaterialTheme.colorScheme.inversePrimary else defaultColor,
                content = content,
            )
        } else {
            Surface(
                modifier = modifier,
                content = content,
                color = if (isActive || isSelected) MaterialTheme.colorScheme.inversePrimary else defaultColor,
            )
        }
    }

    CustomContainer(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(paddingValues).height(IntrinsicSize.Min),
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
                        MediaCoverImageWidget(
                            modifier = Modifier.size(50.dp).background(MaterialTheme.colorScheme.surfaceDim),
                            model = thumbnailSourceUri,
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

            if (isSelected) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null)
                }
            } else {
                when (actionType) {
                    ActionType.NONE -> {
                        Spacer(Modifier.size(48.dp))
                    }

                    ActionType.OPTION -> {
                        IconButton(
                            onClick = {
                                onOptionButtonClick?.invoke()
                            },
                        ) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "menu")
                        }
                    }

                    ActionType.SWAP -> {
                        Icon(
                            modifier = Modifier.padding(12.dp).then(swapIconModifier!!),
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "swap",
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ListTileItemViewPreview() {
    MelodifyTheme {
        ListTileItemView(
            title = "Title",
            subTitle = "sub Title",
        )
    }
}

@Preview
@Composable
private fun ListTileItemViewActivePreview() {
    MelodifyTheme {
        ListTileItemView(
            title = "Title",
            subTitle = "sub Title",
            isActive = true,
        )
    }
}

@Preview
@Composable
private fun ListTileItemViewSelectedPreview() {
    MelodifyTheme {
        ListTileItemView(
            title = "Title",
            subTitle = "sub Title",
            isSelected = true,
        )
    }
}
