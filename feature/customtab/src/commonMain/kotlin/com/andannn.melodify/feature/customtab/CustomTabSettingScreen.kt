package com.andannn.melodify.feature.customtab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.feature.common.util.getCategoryResource
import com.andannn.melodify.feature.common.util.rememberSwapListState
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem

@Composable
internal fun CustomTabSettingScreen(
    stateHolder: CustomTabSettingViewStateHolder = rememberCustomTabSettingViewStateHolder(),
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    val state by stateHolder.state.collectAsState()

    CustomTabSettingContent(
        modifier = modifier,
        state = state,
        onBackPressed = onBackPressed,
        onEvent = stateHolder::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun CustomTabSettingContent(
    state: TabUiState,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onEvent: (UiEvent) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Custom Tab Setting"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        onEvent(UiEvent.OnResetClick)
                    }) {
                        Text("Reset")
                    }
                }
            )
        }
    ) {
        val currentCustomTabs by rememberUpdatedState(state.currentTabs)
        val allAvailableTabSectors by rememberUpdatedState(state.allAvailableTabSectors)

        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            TabOrderPreview(
                modifier = Modifier.weight(1f),
                tabs = currentCustomTabs,
                onUpdateTabs = { onEvent(UiEvent.OnUpdateTabs(it)) }
            )

            LazyColumn(
                modifier = Modifier.weight(2f),
                contentPadding = PaddingValues(bottom = 200.dp)
            ) {
                allAvailableTabSectors.forEach { sector ->
                    stickyHeader {
                        Header(title = stringResource(sector.sectorTitle))
                    }

                    items(
                        sector.sectorContent,
                        key = { it.toString() },
                    ) { tab ->
                        SelectTabItem(
                            title = getCategoryResource(tab),
                            selected = currentCustomTabs.contains(tab),
                            onClick = {
                                onEvent(
                                    UiEvent.OnSelectedChange(
                                        tab,
                                        !currentCustomTabs.contains(tab)
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabOrderPreview(
    modifier: Modifier = Modifier,
    tabs: List<CustomTab>,
    onUpdateTabs: (List<CustomTab>) -> Unit = {}
) {
    val previewListState = rememberSwapListState<CustomTab>(
        onDeleteFinished = { _, newList ->
            onUpdateTabs(newList)
        },
        onSwapFinished = { _, _, newList ->
            onUpdateTabs(newList)
        }
    )
    LaunchedEffect(tabs) {
        previewListState.onApplyNewList(tabs.toImmutableList())
    }

    val newList by rememberUpdatedState(previewListState.itemList.toList())
    var oldList by remember {
        mutableStateOf(tabs)
    }
    LaunchedEffect(newList) {
        if (oldList.isNotEmpty() && newList.size > oldList.size) {
            previewListState.lazyListState.animateScrollToItem(previewListState.itemList.size - 1)
        }
        oldList = newList
    }

    Card(
        modifier = modifier.fillMaxSize().padding(6.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface
        )
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = previewListState.lazyListState,
        ) {
            items(
                previewListState.itemList,
                key = { it.toString() }
            ) { tab ->
                ReorderableItem(
                    state = previewListState.reorderableLazyListState,
                    key = tab.toString()
                ) {
                    PreviewItem(
                        modifier = Modifier,
                        reorderModifier = Modifier.draggableHandle(
                            onDragStopped = {
                                previewListState.onStopDrag()
                            }
                        ),
                        title = getCategoryResource(tab),
                        onDelete = {
                            previewListState.onDeleteItem(tab)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewItem(
    title: String,
    modifier: Modifier = Modifier,
    reorderModifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                )
            }

            Text(
                modifier = Modifier.padding(vertical = 16.dp).weight(1f),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = TextDecoration.Underline
            )

            IconButton(
                modifier = reorderModifier,
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun SelectTabItem(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val targetColor = if (selected) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surface
    }

    val color by animateColorAsState(targetColor)

    Surface(
        modifier = modifier,
        onClick = onClick,
        color = color
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 12.dp).weight(1f),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )

            Box(
                modifier = Modifier.fillMaxHeight().width(64.dp),
            ) {
                AnimatedContent(
                    modifier = Modifier.fillMaxSize(),
                    targetState = selected,
                ) { selected ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (selected) {
                            Icon(
                                modifier = Modifier.fillMaxSize().align(Alignment.Center),
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
