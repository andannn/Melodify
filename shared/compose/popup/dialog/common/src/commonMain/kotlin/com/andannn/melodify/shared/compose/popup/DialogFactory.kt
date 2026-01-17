/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class DialogFactoryProvider : PopupFactoryProvider {
    override fun create(entry: PopupEntry<*>): PopupFactory? =
        (entry.metadata[ALERT_DIALOG_KEY] as? DialogProperties)?.let {
            DialogFactory(
                entry = entry,
                dialogProperties = it,
            )
        }

    companion object {
        internal const val ALERT_DIALOG_KEY = "alert_dialog"

        fun metadata(dialogProperties: DialogProperties = DialogProperties()): Map<String, Any> =
            mapOf(ALERT_DIALOG_KEY to dialogProperties)
    }
}

@Immutable
internal class DialogFactory(
    private val entry: PopupEntry<*>,
    private val dialogProperties: DialogProperties,
) : PopupFactory {
    @Composable
    override fun PopupFactoryScope.Content() {
        Dialog(
            onDismissRequest = onRequestDismiss,
            properties = dialogProperties,
            content = {
                entry.Content(
                    onAction = onPerformAction,
                )
            },
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DialogFactory

        return entry == other.entry &&
            dialogProperties == other.dialogProperties
    }

    override fun hashCode(): Int =
        entry.hashCode() * 31 +
            dialogProperties.hashCode() * 31

    override fun toString(): String = "DialogFactory(entry=$entry, dialogProperties=$dialogProperties)"
}
