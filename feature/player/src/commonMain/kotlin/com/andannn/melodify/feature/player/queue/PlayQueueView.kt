package com.andannn.melodify.feature.player.queue

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.feature.common.widgets.ActionType
import com.andannn.melodify.feature.common.widgets.ListTileItemView
import com.andannn.melodify.feature.common.util.rememberSwapListState
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem

private const val TAG = "PlayQueueView"

@Composable
fun PlayQueue(
    modifier: Modifier = Modifier,
    stateHolder: PlayQueueStateHolder = rememberPlayQueueStateHolder(),
) {
    PlayQueueContent(
        modifier = modifier,
        onItemClick = stateHolder::onItemClick,
        onSwapFinished = stateHolder::onSwapFinished,
        onDeleteFinished = stateHolder::onDeleteFinished,
        playListQueue = stateHolder.playListQueue.toImmutableList(),
        activeMediaItem = stateHolder.interactingMusicItem,
    )
}

@Composable
private fun PlayQueueContent(
    onItemClick: (AudioItemModel) -> Unit,
    onSwapFinished: (from: Int, to: Int) -> Unit,
    onDeleteFinished: (Int) -> Unit,
    playListQueue: ImmutableList<AudioItemModel>,
    activeMediaItem: AudioItemModel,
    modifier: Modifier = Modifier,
) {
    val playQueueState =
        rememberSwapListState<AudioItemModel>(
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
            modifier
                .fillMaxWidth(),
        state = playQueueState.lazyListState,
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
                    isActive = item.extraUniqueId == activeMediaItem.extraUniqueId,
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
    item: AudioItemModel,
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
            defaultColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            albumArtUri = item.artWorkUri,
            title = item.name,
            showTrackNum = false,
            subTitle = item.artist,
            trackNum = item.cdTrackNumber,
            actionType = ActionType.SWAP,
            onMusicItemClick = onClick,
        )
    }
}
