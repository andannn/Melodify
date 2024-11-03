package com.andannn.melodify.feature.message

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED


sealed interface InteractionResult {
    data object ACCEPT : InteractionResult

    data object DISMISS : InteractionResult

    data object DECLINE : InteractionResult
}

interface MessageController {
    val sendDialogChannel: Channel<MessageDialog>

    suspend fun showMessageDialogAndWaitResult(dialog: MessageDialog): InteractionResult

    fun onResult(dialog: MessageDialog, result: InteractionResult)

    fun close()
}

class MessageControllerImpl : MessageController {
    override val sendDialogChannel: Channel<MessageDialog> = Channel(capacity = UNLIMITED)
    private val resultChannelMap: MutableMap<MessageDialog, Channel<InteractionResult>> = mutableMapOf()

    override suspend fun showMessageDialogAndWaitResult(dialog: MessageDialog): InteractionResult {
        if (resultChannelMap.containsKey(dialog)) {
            throw IllegalArgumentException("Dialog $dialog is already shown")
        }

        sendDialogChannel.trySend(dialog)

        try {
            val resultChannel = Channel<InteractionResult>(BUFFERED)
            resultChannelMap[dialog] = resultChannel

            return resultChannel.receive()
        } catch (exception: CancellationException) {
            resultChannelMap.remove(dialog)
            throw exception
        }
    }

    override fun onResult(dialog: MessageDialog, result: InteractionResult) {
        if (!resultChannelMap.containsKey(dialog)) {
            // dialog now shown or request canceled
            return
        }

        val channel = resultChannelMap.remove(dialog)
        channel?.trySend(result)
    }

    override fun close() {
        sendDialogChannel.close()
        resultChannelMap.values.forEach {
            it.close()
        }
    }
}
