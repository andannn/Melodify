/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.RetainedPresenter

@Composable
fun rememberTabManagementScreenPresenter(navigator: Navigator): Presenter<TabManagementScreenState> =
    retain(
        navigator,
    ) {
        TabManagementScreenPresenter(
            navigator = navigator,
        )
    }

@Stable
data class TabManagementScreenState(
    val eventSink: (UiEvent) -> Unit,
)

sealed interface UiEvent {
    data object OnBackKeyPressed : UiEvent
}

private class TabManagementScreenPresenter(
    private val navigator: Navigator,
) : RetainedPresenter<TabManagementScreenState>() {
    @Composable
    override fun present(): TabManagementScreenState =
        TabManagementScreenState { event ->
            when (event) {
                UiEvent.OnBackKeyPressed -> navigator.popBackStack()
            }
        }
}
