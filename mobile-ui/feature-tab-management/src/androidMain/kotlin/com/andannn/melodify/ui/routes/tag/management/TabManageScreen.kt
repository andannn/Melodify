/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.tag.management

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
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.popup.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.components.tabmanagement.TabManagementUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TabManagementScreen(
    navigator: Navigator,
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
        TabManagementUi(
            modifier = Modifier.padding(it),
        )
    }
}
