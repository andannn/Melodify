package com.andannn.melodify.ui.common.components.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
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
                    Tab(
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