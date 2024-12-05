package com.andannn.melodify.ui.components.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.common.util.getCategoryResource

@Composable
fun ReactiveTab(
    modifier: Modifier = Modifier,
    stateHolder: TabUiStateHolder = rememberTabUiStateHolder()
) {
    val state = stateHolder.state
    val tabs by rememberUpdatedState(state.customTabList)
    val selectedIndex by rememberUpdatedState(state.selectedIndex)

    Column(
        modifier = modifier
    ) {
        if (tabs.isNotEmpty()) {
            ScrollableTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = selectedIndex,
                indicator =
                @Composable { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions.getOrElse(selectedIndex) { tabPositions.last() })
                    )
                },
            ) {
                tabs.forEachIndexed { index, item ->
//                    CustomTab(
//                        modifier = Modifier,
//                        label = getCategoryResource(item),
//                        isSelected = index == selectedIndex,
//                        onClick = {
//                            stateHolder.onClickTab(index)
//                        },
//                        onLongClick = {
//                            stateHolder.onShowTabOption(item)
//                        }
//                    )
                    Tab(
                        modifier = Modifier,
                        selected = index == selectedIndex,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                        text = @Composable {
                            Text(
                                text = getCategoryResource(item),
                            )
                        },
                        onClick = {
                            stateHolder.onClickTab(index)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomTab(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Text(
                text = label,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}