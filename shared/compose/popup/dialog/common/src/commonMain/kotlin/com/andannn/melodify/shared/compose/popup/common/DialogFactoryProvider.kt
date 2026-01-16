/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.common

import androidx.compose.runtime.Immutable
import androidx.compose.ui.window.DialogProperties

interface DialogFactoryProvider {
    fun create(entry: DialogEntry<*>): DialogFactory?
}

@Immutable
object AlertDialogFactoryProvider : DialogFactoryProvider {
    override fun create(entry: DialogEntry<*>): DialogFactory? =
        entry.dialogType.takeIf { it == DialogType.AlertDialog }?.let {
            AlertDialogFactory(
                entry = entry,
                dialogProperties =
                    DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                    ),
            )
        }
}

@Immutable
object ModalBottomSheetFactoryProvider : DialogFactoryProvider {
    override fun create(entry: DialogEntry<*>): DialogFactory? =
        entry.dialogType.takeIf { it == DialogType.ModalBottomSheet }?.let {
            ModalBottomSheetFactory(
                entry = entry,
            )
        }
}
