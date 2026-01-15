/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.land.player.queue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.components.queue.PlayQueue
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.play_queue
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun QueueWithHeader(
    modifier: Modifier = Modifier,
    onCloseQueue: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxHeight().width(360.dp),
    ) {
        Header(onCloseQueue = onCloseQueue)
        PlayQueue()
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    onCloseQueue: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(vertical = 8.dp)
                .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(Res.string.play_queue),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = {
                onCloseQueue()
            },
        ) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun HeaderPreview() {
    MelodifyTheme {
        Header()
    }
}
