/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.RootNavigator
import com.andannn.melodify.ui.components.tabmanagement.TabManagementContent
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabManagementScreen(
    navigator: RootNavigator,
    modifier: Modifier = Modifier,
    presenter: Presenter<TabManagementScreenState> = rememberTabManagementScreenPresenter(navigator),
) {
    val state = presenter.present()
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(rememberAndSetupSnackBarHostState())
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Manage Tabs")
                },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink.invoke(UiEvent.OnBackKeyPressed) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        TabManagementContent(
            modifier = Modifier.padding(it),
        )
    }

    ActionDialogContainer()
}
