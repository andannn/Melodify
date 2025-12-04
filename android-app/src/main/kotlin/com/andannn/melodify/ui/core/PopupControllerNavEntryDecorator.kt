/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.NavEntryDecorator
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer

@Composable
fun <T : Any> rememberPopupControllerNavEntryDecorator(): NavEntryDecorator<T> = remember { PopupControllerNavEntryDecorator() }

private class PopupControllerNavEntryDecorator<T : Any> :
    NavEntryDecorator<T>(
        onPop = {
        },
        decorate = { entry ->
            val holder = retain { PopupController() }
            CompositionLocalProvider(
                LocalPopupController provides holder,
            ) {
                entry.Content()

                ActionDialogContainer()
            }
        },
    )
