/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.tabmanage

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.andannn.melodify.ui.components.tabmanagement.TabManagementContent
import com.andannn.melodify.ui.core.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.andannn.melodify.windows.CustomMenuBar
import com.andannn.melodify.windows.WindowNavigator
import com.andannn.melodify.windows.handleMenuEvent

@Composable
fun TabManageWindow(
    navigator: WindowNavigator,
    onCloseRequest: () -> Unit,
) {
    Window(
        onCloseRequest = onCloseRequest,
        title = "Manage Tabs",
    ) {
        CustomMenuBar(navigator::handleMenuEvent)

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = rememberAndSetupSnackBarHostState(),
                    modifier = Modifier.padding(bottom = 64.dp),
                )
            },
        ) {
            TabManagementContent(
                modifier = Modifier.padding(it),
            )
        }

        ActionDialogContainer()
    }
}
