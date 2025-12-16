/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal expect fun isIOSCustomMPLibraryUri(uri: String): Boolean

@Composable
internal expect fun IOSMediaArtworkView(
    modifier: Modifier = Modifier,
    coverUri: String,
)
