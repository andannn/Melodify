/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.common.util.getCategoryResource
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabUi(
    state: TabUiState,
    modifier: Modifier = Modifier,
    popupController: PopupController = LocalPopupController.current,
) {
    fun onTabManageClick() {
    }

    val tabs = state.customTabList
    val selectedIndex = state.selectedIndex

    Box(
        modifier = modifier.fillMaxWidth(),
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

        IconButton(
            modifier = Modifier.padding(start = 4.dp),
            onClick = {
                onTabManageClick()
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = "menu",
            )
        }
    }
}
