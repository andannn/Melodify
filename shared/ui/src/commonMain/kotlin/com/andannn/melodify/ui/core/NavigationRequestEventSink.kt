/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.andannn.melodify.model.LibraryDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

sealed interface NavigationRequest {
    data class GoToLibraryDetail(
        val dataSource: LibraryDataSource,
    ) : NavigationRequest
}

interface NavigationRequestEventSink {
    val channel: ReceiveChannel<NavigationRequest>

    suspend fun onRequestNavigate(event: NavigationRequest)
}

val LocalNavigationRequestEventSink: ProvidableCompositionLocal<NavigationRequestEventSink> =
    staticCompositionLocalOf {
        error("NavigationRequestEventSink not provided")
    }

fun NavigationRequestEventSink(): NavigationRequestEventSink = ChannelNavigationRequestEventChannel()

private class ChannelNavigationRequestEventChannel : NavigationRequestEventSink {
    private val requestChannel = Channel<NavigationRequest>()

    override val channel: ReceiveChannel<NavigationRequest> = requestChannel

    override suspend fun onRequestNavigate(event: NavigationRequest) {
        requestChannel.send(event)
    }
}
