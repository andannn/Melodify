package com.andannn.melodify.navigation.routes


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.ui.common.util.getCategoryResource
import com.andannn.melodify.ui.common.util.rememberSwapListState
import com.andannn.melodify.ui.components.tabselector.CustomTabSettingViewStateHolder
import com.andannn.melodify.ui.components.tabselector.TabUiState
import com.andannn.melodify.ui.components.tabselector.UiEvent
import com.andannn.melodify.ui.components.tabselector.rememberCustomTabSettingViewStateHolder
import kotlinx.collections.immutable.toImmutableList
import sh.calvin.reorderable.ReorderableItem

const val CUSTOM_TAB_SETTING_ROUTE = "custom_tab_setting_route"

fun NavController.navigateToCustomTabSetting() {
    this.navigate(CUSTOM_TAB_SETTING_ROUTE)
}

fun NavGraphBuilder.customTabSetting(onBackPressed: () -> Unit) {
    composable(
        route = CUSTOM_TAB_SETTING_ROUTE,
    ) {
        CustomTabSettingScreen(onBackPressed = onBackPressed)
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
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
                        text = "Reorder Tabs",
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
            )
        }
    ) {
        val currentCustomTabs by rememberUpdatedState(state.currentTabs)

        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            TabOrderPreview(
                modifier = Modifier.padding(16.dp),
                tabs = currentCustomTabs,
                onUpdateTabs = { onEvent(UiEvent.OnUpdateTabs(it)) }
            )
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
                )
            }
        }
    }
}

@Composable
private fun PreviewItem(
    title: String,
    modifier: Modifier = Modifier,
    reorderModifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
