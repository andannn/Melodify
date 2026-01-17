/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.Composable

interface PopupFactory {
    @Composable
    fun PopupFactoryScope.Content()
}

data class PopupFactoryScope(
    val onRequestDismiss: () -> Unit,
    val onPerformAction: (Any) -> Unit,
)

interface PopupFactoryProvider {
    fun create(entry: PopupEntry<*>): PopupFactory?
}
