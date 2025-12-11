/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

enum class DialogType {
    AlertDialog,
    ModalBottomSheet,
    DropDownDialog,
}

internal expect val DialogId<*>.dialogType: DialogType
