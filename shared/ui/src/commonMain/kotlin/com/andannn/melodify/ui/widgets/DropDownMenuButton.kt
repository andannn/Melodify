/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DropDownMenuIconButton(
    modifier: Modifier = Modifier,
    options: List<StringResource>,
    enabled: Boolean = true,
    onSelectIndex: (index: Int) -> Unit,
    // Hide selected item by default
    selectedIndex: Int? = null,
    imageVector: ImageVector = Icons.Default.FilterAlt,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier =
        modifier,
    ) {
        TextButton(enabled = enabled, onClick = { expanded = !expanded }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(imageVector, contentDescription = "Filter")
                if (selectedIndex != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(options[selectedIndex]))
                }
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(stringResource(item)) },
                    onClick = {
                        onSelectIndex(index)
                        expanded = false
                    },
                )
            }
        }
    }
}
