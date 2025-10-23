package com.andannn.melodify.ui.components.tabmanagement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.ui.common.util.getCategoryResource
import com.andannn.melodify.ui.common.util.rememberSwapListState
import com.andannn.melodify.ui.common.widgets.ActionType
import com.andannn.melodify.ui.common.widgets.ListTileItemView
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem

private const val TAG = "TabManagement"

@Composable
fun TabManagement(
    state: TabManagementState,
    modifier: Modifier = Modifier,
) {
    TabManagementContent(
        modifier = modifier,
        currentTabList = state.tabList,
        onSwapFinished = { from, to ->
            state.eventSink.invoke(TabManagementEvent.OnSwapFinished(from, to))
        },
        onDeleteFinished = { index ->
            state.eventSink.invoke(TabManagementEvent.OnDeleteFinished(index))
        },
        onBackKeyPressed = {
            state.eventSink.invoke(TabManagementEvent.OnBackKeyPressed)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabManagementContent(
    modifier: Modifier,
    currentTabList: ImmutableList<CustomTab>,
    onSwapFinished: (from: Int, to: Int) -> Unit = { _, _ -> },
    onDeleteFinished: (index: Int) -> Unit = { },
    onBackKeyPressed: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Manage Tabs")
                },
                navigationIcon = {
                    IconButton(onClick = onBackKeyPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        val listState: LazyListState =
            rememberLazyListState()
        val customTabState =
            rememberSwapListState<CustomTab>(
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

        LaunchedEffect(currentTabList) {
            customTabState.onApplyNewList(currentTabList)
        }

        LazyColumn(
            modifier =
                modifier.padding(it).fillMaxSize(),
            state = listState,
        ) {
            items(
                items = customTabState.itemList,
                key = { it.hashCode() },
            ) { item ->
                ReorderableItem(
                    state = customTabState.reorderableLazyListState,
                    key = item.hashCode(),
                ) { _ ->
                    CustomTabItem(
                        item = item,
                        onSwapFinish = {
                            customTabState.onStopDrag()
                        },
                        onDeleteItem = {
                            customTabState.onDeleteItem(item)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.CustomTabItem(
    item: CustomTab,
    modifier: Modifier = Modifier,
    onDeleteItem: () -> Unit = {},
    onSwapFinish: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp)) {
            if (item !is CustomTab.AllMusic) {
                IconButton(
                    modifier = Modifier.padding(start = 10.dp),
                    onClick = onDeleteItem,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = Color.Red,
                        contentDescription = "Delete",
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        ListTileItemView(
            modifier = modifier,
            actionType = ActionType.SWAP,
            swapIconModifier =
                Modifier.draggableHandle(
                    onDragStopped = onSwapFinish,
                ),
            title = getCategoryResource(item),
        )
    }
}
