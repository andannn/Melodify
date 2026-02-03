/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.search_your_library
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInputField(
    state: SearchBarLayoutState,
    isFullScreen: Boolean = true,
) {
    SearchBarDefaults.InputField(
        textFieldState = state.textFieldState,
        searchBarState = state.searchBarState,
        onSearch = {
            state.eventSink.invoke(SearchBarUiEvent.OnConfirmSearch(it))
        },
        placeholder = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Text(text = stringResource(Res.string.search_your_library))
            }
        },
        leadingIcon = {
            if (isFullScreen) {
                SearchLeadingIcon(
                    searchBarState = state.searchBarState,
                    onBackClick = {
                        state.eventSink.invoke(SearchBarUiEvent.OnBackFullScreen)
                    },
                )
            }
        },
        trailingIcon = {
            Spacer(Modifier.width(48.dp))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchLeadingIcon(
    searchBarState: SearchBarState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier.graphicsLayer {
                alpha = searchBarState.progress
            },
    ) {
        if (searchBarState.progress != 0f) {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = { PlainTooltip { Text("Back") } },
                state = rememberTooltipState(),
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }
    }
}
