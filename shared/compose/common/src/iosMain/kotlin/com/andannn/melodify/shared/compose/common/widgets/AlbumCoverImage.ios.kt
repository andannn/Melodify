/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun MediaCoverImage(
    model: String?,
    modifier: Modifier,
) {
    if (model == null) return

    if (isIOSCustomMPLibraryUri(model)) {
        IOSMediaArtworkView(
            modifier = Modifier.fillMaxSize(),
            coverUri = model,
        )
    } else {
        AsyncImageImpl(model, modifier)
    }
}
