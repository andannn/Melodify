/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent.header

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.handleMediaOptionClick
import kotlinx.coroutines.launch
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun GroupHeader(
    state: GroupHeaderState,
    popupController: PopupController = LocalPopupController.current,
    repository: Repository = LocalRepository.current,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    HeaderInfo(
        modifier = modifier,
        coverArtUri = state.cover,
        title = state.title,
        trackCount = state.trackCount,
        onOptionClick = {
            scope.launch {
                state.mediaItem?.let { media ->
                    when (media) {
                        is AlbumItemModel -> {
                            val result =
                                popupController.showDialog(DialogId.SearchedAlbumOption(media))
                            if (result is DialogAction.MediaOptionDialog.ClickItem) {
                                with(repository) {
                                    with(popupController) {
                                        handleMediaOptionClick(
                                            optionItem = result.optionItem,
                                            dialog = result.dialog,
                                        )
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        },
    )
}

@Composable
private fun HeaderInfo(
    modifier: Modifier = Modifier,
    coverArtUri: String = "",
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
            AsyncImage(
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.extraSmall),
                model = coverArtUri,
                placeholder = painterResource(defaultImagePlaceholderRes),
                error = painterResource(defaultImagePlaceholderRes),
                contentDescription = "",
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    Text(
                        modifier = Modifier.align(Alignment.Bottom),
                        text = "$trackCount tracks",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier.padding(end = 6.dp),
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
