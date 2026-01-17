/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.NavEntryDecorator
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.ModalBottomSheetFactoryProvider
import com.andannn.melodify.shared.compose.popup.entry.alert.alertDialogEntry
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
import io.github.andannn.popup.PopupHostState
import io.github.andannn.popup.entryProvider

@Composable
internal fun <T : Any> rememberPopupControllerNavEntryDecorator(): NavEntryDecorator<T> = remember { PopupControllerNavEntryDecorator() }

private class PopupControllerNavEntryDecorator<T : Any> :
    NavEntryDecorator<T>(
        onPop = {
        },
        decorate = { entry ->
            val popupHostState = retain { PopupHostState() }
            CompositionLocalProvider(
                LocalPopupHostState provides popupHostState,
            ) {
                entry.Content()

                PopupHost(
                    popupHostState = popupHostState,
                    popupFactoryProvider =
                        listOf(
                            DialogFactoryProvider(),
                            ModalBottomSheetFactoryProvider(),
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
                        },
                )
            }
        },
    )
