/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun IOSMediaArtworkView(
    modifier: Modifier,
    coverUri: String,
) {
    error("IOSMediaArtworkView is not supported on deskTop")
}

internal actual fun isIOSCustomMPLibraryUri(uri: String): Boolean = false
