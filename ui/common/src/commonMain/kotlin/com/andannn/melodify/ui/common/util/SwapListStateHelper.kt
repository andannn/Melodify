package com.andannn.melodify.ui.common.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

private const val TAG = "SwapListState"

@Composable
fun <T> rememberSwapListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onSwapFinished: (from: Int, to: Int, newList: List<T>) -> Unit = { _, _, _ -> },
    onDeleteFinished: (index: Int, newList: List<T>) -> Unit = { _, _ -> },
): SwapListStateHolder<T> {
    val onSwapFinishedState = rememberUpdatedState(onSwapFinished)
    val onDeleteFinishedState = rememberUpdatedState(onDeleteFinished)
    val playQueueState =
        remember {
            SwapListStateImpl(
                onSwapFinishedState = onSwapFinishedState,
                onDeleteFinishedState = onDeleteFinishedState,
            )
        }

    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            playQueueState.onSwapItem(from.index, to.index)
        }

    return remember {
        SwapListStateHolder(
            lazyListState = lazyListState,
            swapListState = playQueueState,
            reorderableLazyListState = reorderableLazyListState,
        )
    }
}

class SwapListStateHolder<T>(
    val lazyListState: LazyListState,
    private val swapListState: SwapListStateImpl<T>,
    val reorderableLazyListState: ReorderableLazyListState,
) : SwapListState<T> by swapListState

interface SwapListState<T> {
    val itemList: SnapshotStateList<T>

    fun onStopDrag()

    fun onSwapItem(
        from: Int,
        to: Int,
    )

    fun onApplyNewList(list: ImmutableList<T>)

    fun onDeleteItem(item: T)
}

class SwapListStateImpl<T>(
    private val onSwapFinishedState: State<(from: Int, to: Int, newList: List<T>) -> Unit>,
    private val onDeleteFinishedState: State<(index: Int, newList: List<T>) -> Unit>,
) : SwapListState<T> {
    override val itemList = mutableStateListOf<T>()

    private var fromDragIndex: Int? = null
    private var toDragIndex: Int? = null

    override fun onStopDrag() {
        Napier.d(tag = TAG) { "PlayQueueView: drag stopped" }
        val to = toDragIndex
        val from = fromDragIndex

        if (to != null && from != null) {
            onSwapFinishedState.value(from, to, itemList.toList())
        }

        fromDragIndex = null
        toDragIndex = null
    }

    override fun onSwapItem(
        from: Int,
        to: Int,
    ) {
        Napier.d(tag = TAG) { "PlayQueueView: onSwapItem from $from to $to" }
        toDragIndex = to
        if (fromDragIndex == null) {
            fromDragIndex = from
        }

        with(itemList) {
            add(to, removeAt(from))
        }
    }

    override fun onApplyNewList(list: ImmutableList<T>) {
        Napier.d(tag = TAG) { "onApplyNewList $list" }
        with(itemList) {
            clear()
            addAll(list)
        }
    }

    override fun onDeleteItem(item: T) {
        Napier.d(tag = TAG) { "onDismissItem $item" }
        with(itemList) {
            val index = indexOf(item)
            removeAt(index)
            onDeleteFinishedState.value(index, itemList.toList())
        }
    }
}
