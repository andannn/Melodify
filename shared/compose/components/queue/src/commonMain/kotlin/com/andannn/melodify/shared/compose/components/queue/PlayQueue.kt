/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.queue

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.extraUniqueId
import com.andannn.melodify.domain.model.subTitle
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.util.rememberSwapListState
import com.andannn.melodify.shared.compose.common.widgets.ActionType
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem

private const val TAG = "PlayQueueView"

@Composable
fun PlayQueue(
    modifier: Modifier = Modifier,
    presenter: Presenter<PlayQueueState> = rememberPlayQueuePresenter(),
) {
    PlayQueueUi(
        state = presenter.present(),
        modifier = modifier,
    )
}

@Composable
private fun PlayQueueUi(
    state: PlayQueueState,
    modifier: Modifier = Modifier,
) {
    if (state.playListQueue.isNotEmpty()) {
        PlayQueueContent(
            modifier = modifier,
            onItemClick = {
                state.eventSink.invoke(PlayQueueEvent.OnItemClick(it))
            },
            onSwapFinished = { from, to ->
                state.eventSink.invoke(PlayQueueEvent.OnSwapFinished(from = from, to = to))
            },
            onDeleteFinished = {
                state.eventSink.invoke(PlayQueueEvent.OnDeleteFinished(it))
            },
            playListQueue = state.playListQueue.toImmutableList(),
            activeMediaItem = state.interactingMusicItem,
        )
    }
}

@Composable
private fun PlayQueueContent(
    onItemClick: (MediaItemModel) -> Unit,
    onSwapFinished: (from: Int, to: Int) -> Unit,
    onDeleteFinished: (Int) -> Unit,
    playListQueue: ImmutableList<MediaItemModel>,
    activeMediaItem: MediaItemModel?,
    modifier: Modifier = Modifier,
) {
    val playingIndex = playListQueue.indexOfFirst { it == activeMediaItem }
    Napier.d(tag = TAG) { "playingIndex $playingIndex" }
    val listState: LazyListState =
        rememberLazyListState(if (playingIndex == -1) 0 else playingIndex)
    val playQueueState =
        rememberSwapListState<MediaItemModel>(
            lazyListState = listState,
            onSwapFinished = { from, to, _ ->
                Napier.d(tag = TAG) { "PlayQueueView: drag stopped from $from to $to" }
                onSwapFinished(from, to)
            },
            onDeleteFinished = { index, _ ->
                Napier.d(tag = TAG) { "onDeleteFinished $index" }
                onDeleteFinished(index)
            },
        )

    LaunchedEffect(playListQueue) {
        playQueueState.onApplyNewList(playListQueue)
    }

    LazyColumn(
        modifier =
            modifier.fillMaxWidth(),
        state = listState,
    ) {
        items(
            items = playQueueState.itemList,
            key = { it.hashCode() },
        ) { item ->
            ReorderableItem(
                state = playQueueState.reorderableLazyListState,
                key = item.hashCode(),
            ) { _ ->
                QueueItem(
                    item = item,
                    isActive = item.extraUniqueId == activeMediaItem?.extraUniqueId,
                    onClick = {
                        onItemClick(item)
                    },
                    onSwapFinish = {
                        playQueueState.onStopDrag()
                    },
                    onDismissFinish = {
                        playQueueState.onDeleteItem(item)
                    },
                )
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.QueueItem(
    item: MediaItemModel,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onSwapFinish: () -> Unit = {},
    onClick: () -> Unit = {},
    onDismissFinish: () -> Unit = {},
) {
    val dismissState = rememberSwipeToDismissBoxState()
    var dismissed by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(dismissState.currentValue) {
        if (dismissed) return@LaunchedEffect

        val state = dismissState.currentValue
        if (state == SwipeToDismissBoxValue.EndToStart || state == SwipeToDismissBoxValue.StartToEnd) {
            onDismissFinish()
            dismissed = true
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Spacer(modifier = Modifier)
        },
    ) {
        ListTileItemView(
            modifier = modifier,
            swapIconModifier =
                Modifier.draggableHandle(
                    onDragStopped = onSwapFinish,
                ),
            isActive = isActive,
            thumbnailSourceUri = item.artWorkUri,
            title = item.name,
            subTitle = item.subTitle,
            actionType = ActionType.SWAP,
            onItemClick = onClick,
        )
    }
}
