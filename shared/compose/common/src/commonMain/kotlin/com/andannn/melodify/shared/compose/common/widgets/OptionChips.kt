/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionChips(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    selected: Boolean,
    text: String,
    onClick: () -> Unit = {},
) {
    val color =
        if (selected) {
            AssistChipDefaults.assistChipColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        } else {
            AssistChipDefaults.assistChipColors()
        }
    AssistChip(
        modifier = modifier.padding(horizontal = 4.dp),
        enabled = enabled,
        onClick = onClick,
        colors = color,
        label = { Text(text) },
    )
}
