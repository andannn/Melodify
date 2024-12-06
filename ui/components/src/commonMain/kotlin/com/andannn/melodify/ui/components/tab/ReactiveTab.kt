package com.andannn.melodify.ui.components.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardControlKey
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
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

@OptIn(ExperimentalMaterial3Api::class)
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
            SecondaryScrollableTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = selectedIndex,
            ) {
                tabs.forEachIndexed { index, item ->
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
                            if (index != selectedIndex) {
                                stateHolder.onClickTab(index)
                            } else {
                                stateHolder.onShowTabOption(item)
                            }
                        },
                    )
                }
            }
        }
    }
}
