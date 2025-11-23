/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.util.getCategoryResource

@Composable
fun TabUi(
    modifier: Modifier = Modifier,
    presenter: Presenter<TabUiState> = retainTabUiPresenter(),
    onTabManagementClick: () -> Unit = {},
) {
    TabUi(
        modifier = modifier,
        state = presenter.present(),
        onTabManagementClick = onTabManagementClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabUi(
    state: TabUiState,
    modifier: Modifier = Modifier,
    onTabManagementClick: () -> Unit = {},
) {
    val tabs = state.customTabList
    val selectedIndex = state.selectedIndex

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        val scrollState = rememberScrollState()
        if (tabs.isNotEmpty()) {
            SecondaryScrollableTabRow(
                scrollState = scrollState,
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = selectedIndex,
            ) {
                tabs.forEachIndexed { index, item ->
                    Tab(
                        modifier = Modifier.testTag("TabItem"),
                        selected = index == selectedIndex,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                        text = @Composable {
                            Text(
                                modifier = Modifier.widthIn(max = 80.dp),
                                text = getCategoryResource(item),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        onClick = {
                            if (index != selectedIndex) {
                                state.eventSink.invoke(TabUiEvent.OnClickTab(index))
                            } else {
                                state.eventSink.invoke(TabUiEvent.OnShowTabOption(item))
                            }
                        },
                    )
                }
            }
        }

        val buttonVisible by
            remember {
                derivedStateOf {
                    scrollState.value == 0
                }
            }

        IconButton(
            modifier = Modifier.padding(start = 4.dp),
            enabled = buttonVisible,
            onClick = onTabManagementClick,
        ) {
            AnimatedVisibility(visible = buttonVisible) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = "menu",
                )
            }
        }
    }
}
