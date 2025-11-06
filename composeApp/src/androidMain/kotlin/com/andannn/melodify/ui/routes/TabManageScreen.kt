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
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import com.andannn.melodify.RootNavigator
import com.andannn.melodify.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.components.tabmanagement.TabManagementContent
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.slack.circuit.runtime.CircuitUiState

@Composable
fun rememberTabManagementScreenPresenter(navigator: RootNavigator) =
    retain(
        navigator,
    ) {
        TabManagementScreenPresenter(
            navigator = navigator,
        )
    }

class TabManagementScreenPresenter(
    private val navigator: RootNavigator,
) : ScopedPresenter<TabManagementScreenState>() {
    @Composable
    override fun present(): TabManagementScreenState =
        TabManagementScreenState { event ->
            when (event) {
                UiEvent.OnBackKeyPressed -> navigator.popBackStack()
            }
        }
}

data class TabManagementScreenState(
    val eventSink: (UiEvent) -> Unit,
) : CircuitUiState

sealed interface UiEvent {
    data object OnBackKeyPressed : UiEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TabManagementScreen(
    state: TabManagementScreenState,
    modifier: Modifier = Modifier,
) {
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
