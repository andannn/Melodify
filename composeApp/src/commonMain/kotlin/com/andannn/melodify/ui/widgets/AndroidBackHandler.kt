/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.runtime.Composable

@Composable
expect fun AndroidBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
