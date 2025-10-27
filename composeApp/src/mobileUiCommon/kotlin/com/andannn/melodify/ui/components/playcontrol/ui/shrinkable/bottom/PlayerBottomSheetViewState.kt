/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.bottom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class SheetTab {
    NEXT_SONG,
    LYRICS,
}

@Composable
internal fun rememberPlayerBottomSheetState() =
    remember {
        PlayerBottomSheetState()
    }

internal class PlayerBottomSheetState {
    var sheetItems by mutableStateOf(SheetTab.entries.toTypedArray())

    var selectedTab by mutableStateOf(SheetTab.NEXT_SONG)

    val selectedIndex: Int
        get() = sheetItems.indexOf(selectedTab)

    fun onSelectItem(item: SheetTab) {
        selectedTab = item
    }
}
