/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.widgets

import androidx.compose.runtime.Composable
import com.slack.circuit.foundation.internal.BackHandler

@Composable
actual fun AndroidBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
