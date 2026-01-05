/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalPlayerStateHolder: ProvidableCompositionLocal<PlayerStateHolder> =
    compositionLocalOf { PlayerStateHolder() }

class PlayerStateHolder {
    var isExpand: Boolean = false
    var isQueueOpened: Boolean = false
}
