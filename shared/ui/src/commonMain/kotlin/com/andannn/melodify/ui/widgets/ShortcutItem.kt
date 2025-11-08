/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.model.ShortcutItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShortcutItem(
    modifier: Modifier = Modifier,
    shortcutItem: ShortcutItem,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmpIcon(
                shortcutItem.iconRes,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(shortcutItem.textRes),
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}
