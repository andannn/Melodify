/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.lyrics.content

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlainLyricsView(
    lyric: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        text = lyric,
    )
}
