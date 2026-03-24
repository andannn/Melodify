/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MultiSelectingAppBar(
    modifier: Modifier = Modifier,
    selectedCount: Int,
    onExitSelecting: () -> Unit = {},
    onOptionClick: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        colors =
            TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        title = {
            Text("$selectedCount selected")
        },
        navigationIcon = {
            IconButton(onClick = onExitSelecting) {
                Icon(Icons.Default.Close, null)
            }
        },
        actions = {
            IconButton(onClick = onOptionClick) {
                Icon(Icons.Default.MoreVert, null)
            }
        },
    )
}

@Preview
@Composable
private fun MultiSelectingAppBarPreview() {
    MelodifyTheme {
        MultiSelectingAppBar(selectedCount = 1)
    }
}
