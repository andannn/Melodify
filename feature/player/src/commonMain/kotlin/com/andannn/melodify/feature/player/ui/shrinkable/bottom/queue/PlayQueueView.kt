package com.andannn.melodify.feature.player.ui.shrinkable.bottom.queue

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.andannn.melodify.feature.common.component.ListTileItemView
import com.andannn.melodify.core.data.model.AudioItemModel
import kotlinx.collections.immutable.ImmutableList
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem

private const val TAG = "PlayQueueView"

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PlayQueue(
    onItemClick: (AudioItemModel) -> Unit,
    onSwapFinished: (from: Int, to: Int) -> Unit,
    onDeleteFinished: (Int) -> Unit,
    playListQueue: ImmutableList<AudioItemModel>,
    activeMediaItem: AudioItemModel,
    modifier: Modifier = Modifier
) {
    val playQueueState = rememberPlayQueueState(
        onSwapFinished = { from, to ->
//            Timber.tag(TAG).d("PlayQueueView: drag stopped from $from to $to")
            onSwapFinished(from, to)
        },
        onDeleteFinished = {
//            Timber.tag(TAG).d("onDeleteFinished $it")
            onDeleteFinished(it)
        }
    )

    LaunchedEffect(playListQueue) {
        playQueueState.onApplyNewList(playListQueue)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        state = playQueueState.lazyListState
    ) {
        items(
            items = playQueueState.audioItemList,
            key = { it.hashCode() },
        ) { item ->
            ReorderableItem(
                state = playQueueState.reorderableLazyListState,
                key = item.hashCode()
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
                        playQueueState.onDismissItem(item)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReorderableCollectionItemScope.QueueItem(
    item: AudioItemModel,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onSwapFinish: () -> Unit = {},
    onClick: () -> Unit = {},
    onDismissFinish: () -> Unit = {}
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
        }
    ) {
        ListTileItemView(
            modifier = modifier,
            swapIconModifier = Modifier.draggableHandle(
                onDragStopped = onSwapFinish
            ),
            isActive = isActive,
            defaultColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            albumArtUri = item.artWorkUri,
            title = item.name,
            showTrackNum = false,
            subTitle = item.artist,
            trackNum = item.cdTrackNumber,
            onMusicItemClick = onClick,
        )
    }
}