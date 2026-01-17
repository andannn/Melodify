/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import io.github.andannn.popup.PopupEntry
import io.github.andannn.popup.PopupFactory
import io.github.andannn.popup.PopupFactoryProvider
import io.github.andannn.popup.PopupFactoryScope

class ModalBottomSheetFactoryProvider : PopupFactoryProvider {
    override fun create(entry: PopupEntry<*>): PopupFactory? =
        entry.metadata[BOTTOM_SHEET_KEY]?.let {
            ModalBottomSheetFactory(
                entry = entry,
            )
        }

    companion object {
        internal const val BOTTOM_SHEET_KEY = "bottom_sheet"

        fun bottomSheet(): Map<String, Any> = mapOf(BOTTOM_SHEET_KEY to Unit)
    }
}

data class ModalBottomSheetFactory(
    private val entry: PopupEntry<*>,
) : PopupFactory {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PopupFactoryScope.Content() {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = onRequestDismiss,
            content = {
                entry.Content(
                    onAction = {
                        onPerformAction(it)
                    },
                )
            },
        )
    }
}
