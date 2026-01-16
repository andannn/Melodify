/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.play.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.shared.compose.common.mock.MockData
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.ActionType
import com.andannn.melodify.shared.compose.common.widgets.LargePreviewCard
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import com.andannn.melodify.shared.compose.common.widgets.SmpTextButton
import com.andannn.melodify.shared.compose.popup.common.DialogEntryProviderScope
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.common.DialogType
import com.andannn.melodify.shared.compose.popup.common.entry
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.all_playlists
import melodify.shared.compose.resource.generated.resources.all_to_playlist_page_title
import melodify.shared.compose.resource.generated.resources.new_playlist_dialog_title
import melodify.shared.compose.resource.generated.resources.selected_songs
import melodify.shared.compose.resource.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource

data class AddMusicsToPlayListDialog(
    val items: List<MediaItemModel>,
    val isAudio: Boolean,
) : DialogId<AddToPlayListDialogResult>

sealed interface AddToPlayListDialogResult {
    data class OnAddToPlayListResult(
        val playList: PlayListItemModel,
        val items: List<MediaItemModel>,
    ) : AddToPlayListDialogResult

    object OnCreateNewPlayListResult : AddToPlayListDialogResult

    object OnDismiss : AddToPlayListDialogResult
}

fun DialogEntryProviderScope<DialogId<*>>.addToPlayListDialogEntry() {
    entry(
        dialogType = DialogType.ModalBottomSheet,
    ) { dialogId, onAction ->
        AddToPlayListDialogContent(
            dialogId,
            onAction = onAction,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddToPlayListDialogContent(
    dialog: AddMusicsToPlayListDialog,
    onAction: (AddToPlayListDialogResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = retainedAddToPlayListSheetState(dialog.isAudio).present()
    AddToPlayListRequestSheetContent(
        modifier = modifier.fillMaxWidth(),
        items = dialog.items,
        playLists = state.playLists,
        onRequestDismiss = {
            onAction(AddToPlayListDialogResult.OnDismiss)
        },
        onPlayListClick = { playList ->
            onAction(
                AddToPlayListDialogResult.OnAddToPlayListResult(
                    playList,
                    dialog.items,
                ),
            )
        },
        onCreateNewClick = {
            onAction(AddToPlayListDialogResult.OnCreateNewPlayListResult)
        },
    )
}

@Composable
internal fun AddToPlayListRequestSheetContent(
    modifier: Modifier = Modifier,
    items: List<MediaItemModel>,
    playLists: List<PlayListItemModel>,
    onRequestDismiss: () -> Unit = {},
    onPlayListClick: (PlayListItemModel) -> Unit = {},
    onCreateNewClick: () -> Unit = {},
) {
    val itemCount by rememberUpdatedState(items.size)

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.all_to_playlist_page_title, itemCount),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onRequestDismiss,
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close")
            }
        }

        HorizontalDivider()

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        text = stringResource(Res.string.selected_songs),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier.padding(top = 16.dp).height(150.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = items,
                            key = { it.id },
                        ) { audio ->
                            LargePreviewCard(
                                modifier = Modifier.width(100.dp),
                                title = audio.name,
                                backGroundColor = Color.Transparent,
                                artCoverUri = audio.artWorkUri ?: "",
                            )
                        }
                    }
                }

                item {
                    Column {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    Text(
                        modifier =
                            Modifier.padding(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 8.dp,
                            ),
                        text = stringResource(Res.string.all_playlists),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                items(
                    items = playLists,
                    key = { it.id },
                ) { playList ->
                    ListTileItemView(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        title = playList.name,
                        subTitle = stringResource(Res.string.track_count, playList.trackCount),
                        thumbnailSourceUri = playList.artWorkUri,
                        actionType = ActionType.NONE,
                        defaultColor = Color.Transparent,
                        onItemClick = {
                            onPlayListClick(playList)
                        },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }

            SmpTextButton(
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                imageVector = Icons.Rounded.Add,
                text = stringResource(Res.string.new_playlist_dialog_title),
                onClick = onCreateNewClick,
            )
        }
    }
}

@Preview
@Composable
private fun AddToPlayListRequestSheetContentPreview() {
    MelodifyTheme {
        Surface {
            AddToPlayListRequestSheetContent(
                items = MockData.medias,
                playLists = MockData.playLists,
            )
        }
    }
}
