/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import io.github.andannn.popup.PopupHostState

val LocalPopupHostState: ProvidableCompositionLocal<PopupHostState> =
    compositionLocalOf { error("No popup controller") }
