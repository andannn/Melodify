/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

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

    fun onRequest(event: NavigationRequest)
}

class ChannelNavigationRequestEventChannel(
    private val coroutineScope: CoroutineScope,
) : NavigationRequestEventSink {
    private val requestChannel = Channel<NavigationRequest>()

    override val channel: ReceiveChannel<NavigationRequest> = requestChannel

    override fun onRequest(event: NavigationRequest) {
        coroutineScope.launch {
            requestChannel.send(event)
        }
    }
}
