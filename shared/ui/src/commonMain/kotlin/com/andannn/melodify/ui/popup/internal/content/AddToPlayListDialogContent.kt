/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.internal.content

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.RetainedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import com.andannn.melodify.ui.popup.AddMusicsToPlayListDialog
import com.andannn.melodify.ui.popup.DialogAction
import com.andannn.melodify.ui.widgets.ActionType
import com.andannn.melodify.ui.widgets.LargePreviewCard
import com.andannn.melodify.ui.widgets.ListTileItemView
import com.andannn.melodify.ui.widgets.SmpTextButton
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.all_playlists
import melodify.shared.ui.generated.resources.all_to_playlist_page_title
import melodify.shared.ui.generated.resources.new_playlist_dialog_title
import melodify.shared.ui.generated.resources.selected_songs
import melodify.shared.ui.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddToPlayListDialogContent(
    modifier: Modifier = Modifier,
    dialog: AddMusicsToPlayListDialog,
    onAction: (DialogAction.AddToPlayListDialog) -> Unit,
) {
    val state = retainedAddToPlayListSheetState(dialog.isAudio).present()
    AddToPlayListRequestSheetContent(
        modifier = modifier.fillMaxWidth(),
        items = dialog.items,
        playLists = state.playLists,
        onRequestDismiss = {
            onAction(DialogAction.AddToPlayListDialog.OnDismiss)
        },
        onPlayListClick = { playList ->
            onAction(
                DialogAction.AddToPlayListDialog.OnAddToPlayList(
                    playList,
                    dialog.items,
                ),
            )
        },
        onCreateNewClick = {
            onAction(DialogAction.AddToPlayListDialog.OnCreateNewPlayList)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun retainedAddToPlayListSheetState(
    isAudio: Boolean,
    repository: Repository = LocalRepository.current,
): Presenter<AddToPlayListSheetState> =
    retainPresenter(isAudio, repository) {
        AddToPlayListSheetPresenter(isAudio, repository)
    }

@OptIn(ExperimentalMaterial3Api::class)
private class AddToPlayListSheetPresenter(
    isAudio: Boolean,
    repository: Repository,
) : RetainedPresenter<AddToPlayListSheetState>() {
    private val playListStateFlow =
        repository
            .getAllPlayListFlow(isAudio)
            .stateIn(
                retainedScope,
                initialValue = emptyList(),
                started = WhileSubscribed(),
            )

    @Composable
    override fun present(): AddToPlayListSheetState {
        val playLists by playListStateFlow.collectAsStateWithLifecycle()
        return AddToPlayListSheetState(playLists)
    }
}

@Stable
private data class AddToPlayListSheetState(
    val playLists: List<PlayListItemModel>,
)
