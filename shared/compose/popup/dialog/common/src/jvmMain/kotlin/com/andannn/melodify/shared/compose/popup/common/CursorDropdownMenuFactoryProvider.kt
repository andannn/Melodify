/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.common

import androidx.compose.runtime.Immutable

@Immutable
object CursorDropdownMenuFactoryProvider : DialogFactoryProvider {
    override fun create(entry: DialogEntry<*>): DialogFactory? =
        entry.dialogType.takeIf { it == DialogType.DropDownDialog }?.let {
            CursorDropdownMenuFactory(
                entry = entry,
            )
        }
}
