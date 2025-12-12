/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.tag.management

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.ui.Navigator

@Composable
internal fun rememberTabManagementScreenPresenter(navigator: Navigator): Presenter<TabManagementScreenState> =
    retain(
        navigator,
    ) {
        TabManagementScreenPresenter(
            navigator = navigator,
        )
    }

@Stable
internal data class TabManagementScreenState(
    val eventSink: (UiEvent) -> Unit,
)

internal sealed interface UiEvent {
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
