/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.andannn.melodify.shared.compose.common.NavigationRequest
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink

interface WindowNavigator {
    fun openWindow(windowType: WindowType)

    fun closeWindow(windowType: WindowType)
}

/**
 * Launch navigation request handler effects.
 *
 * @param navigator
 * @param eventSink
 */
@Composable
fun LaunchNavigationRequestHandlerEffect(
    navigator: WindowNavigator,
    eventSink: NavigationRequestEventSink,
) {
    LaunchedEffect(navigator, eventSink) {
        for (event in eventSink.channel) {
            when (event) {
                is NavigationRequest.GoToLibraryDetail -> {
                    navigator.openWindow(WindowType.MediaLibrary(event.dataSource))
                }
            }
        }
    }
}
