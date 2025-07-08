package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

val LocalPlayerUiController: ProvidableCompositionLocal<PlayerUiController> =
    compositionLocalOf { error("no popup controller") }

fun PlayerUiController(scope: CoroutineScope): PlayerUiController = PlayerUiControllerImpl(scope)

interface PlayerUiController {
    suspend fun expandPlayer()

    suspend fun shrinkPlayer()
}

internal interface PlayerUiEventConsumer {
    val expandEventReceiveChannel: ReceiveChannel<Unit>

    val shrinkEventReceiveChannel: ReceiveChannel<Unit>
}

internal class PlayerUiControllerImpl(
    private val scope: CoroutineScope,
) : PlayerUiController,
    PlayerUiEventConsumer {
    private val expandChannel = Channel<Unit>()
    private val shrinkChannel = Channel<Unit>()
    private val mutex = Mutex()

    override val expandEventReceiveChannel
        get() = expandChannel
    override val shrinkEventReceiveChannel
        get() = shrinkChannel

    override suspend fun expandPlayer(): Unit =
        mutex.withLock {
            scope.launch {
                expandChannel.send(Unit)
            }
        }

    override suspend fun shrinkPlayer(): Unit =
        mutex.withLock {
            scope.launch {
                shrinkChannel.send(Unit)
            }
        }
}
