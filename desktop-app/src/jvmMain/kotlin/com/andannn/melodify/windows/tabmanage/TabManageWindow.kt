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
import com.andannn.melodify.shared.compose.components.tab.management.TabManagementUi
import com.andannn.melodify.shared.compose.popup.ActionDialog
import com.andannn.melodify.shared.compose.popup.snackbar.rememberAndSetupSnackBarHostState
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
            TabManagementUi(
                modifier = Modifier.padding(it),
            )
        }

        ActionDialog()
    }
}
