/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class DialogEntry<T : Any>(
    private val dialogId: T,
    val dialogType: DialogType,
    private val content: @Composable (dialogId: T, onAction: (Any) -> Unit) -> Unit,
) {
    @Composable
    fun Content(onAction: (Any) -> Unit) {
        content(dialogId, onAction)
    }
}
