/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.andannn.melodify.ui.Screen
import io.github.aakira.napier.Napier

private const val TAG = "NavigationRequestEvent"

/**
 * Launch navigation request handler effects.
 *
 * @param navigator
 * @param eventSink
 */
@Composable
fun LaunchNavigationRequestHandlerEffect(
    navigator: Navigator,
    eventSink: NavigationRequestEventSink,
) {
    LaunchedEffect(navigator, eventSink) {
        for (event in eventSink.channel) {
            Napier.d(tag = TAG) { "handle navigation request $event" }
            when (event) {
                is NavigationRequest.GoToLibraryDetail -> {
                    navigator.navigateTo(Screen.LibraryDetail(datasource = event.dataSource))
                }
            }
        }
    }
}
