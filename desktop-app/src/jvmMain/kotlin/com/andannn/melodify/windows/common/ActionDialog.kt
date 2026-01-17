/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.common

import androidx.compose.runtime.Composable
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.ModalBottomSheetFactoryProvider
import com.andannn.melodify.shared.compose.popup.common.CursorDropdownMenuFactoryProvider
import com.andannn.melodify.shared.compose.popup.entry.alert.alertDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.library.addLibraryPathDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.option.addMediaOptionDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.play.list.addToPlayListDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.play.list.newPlayListDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.sleep.timer.sleepTimerCountingDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.sleep.timer.sleepTimerOptionDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.sort.rule.addChangeSortRuleDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.sort.rule.defaultSortRuleSettingDialogEntry
import com.andannn.melodify.shared.compose.popup.entry.sync.syncStatusDialogEntry
import io.github.andannn.popup.DialogFactoryProvider
import io.github.andannn.popup.PopupHost
import io.github.andannn.popup.entryProvider

@Composable
fun CommonActionDialog() {
    PopupHost(
        popupHostState = LocalPopupHostState.current,
        popupFactoryProvider =
            listOf(
                DialogFactoryProvider(),
                ModalBottomSheetFactoryProvider(),
                CursorDropdownMenuFactoryProvider(),
            ),
        entryProvider =
            entryProvider {
                addMediaOptionDialogEntry()
                addChangeSortRuleDialogEntry()
                newPlayListDialogEntry()
                addToPlayListDialogEntry()
                alertDialogEntry()
                defaultSortRuleSettingDialogEntry()
                sleepTimerCountingDialogEntry()
                sleepTimerOptionDialogEntry()
                syncStatusDialogEntry()
                addLibraryPathDialogEntry()
            },
    )
}
