package com.andannn.melodify.ui.components.message

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import com.andannn.melodify.ui.components.message.dialog.Dialog
import com.andannn.melodify.ui.components.message.dialog.InteractionResult
import com.andannn.melodify.ui.components.message.snackbar.SnackBarMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface MessageController {
    val sendDialogChannel: Channel<Dialog>

    val snackBarMessageChannel: ReceiveChannel<SnackbarVisuals>

    val snackBarResultChannel: SendChannel<SnackbarResult>

    suspend fun showMessageDialogAndWaitResult(dialog: Dialog): InteractionResult

    suspend fun showSnackBarAndWaitResult(
        message: SnackBarMessage,
        messageFormatArgs: List<Any> = emptyList()
    ): SnackbarResult

    fun onResult(dialog: Dialog, result: InteractionResult)

    fun close()
}

class MessageControllerImpl : MessageController {
    override val sendDialogChannel: Channel<Dialog> = Channel(capacity = UNLIMITED)
    private val dialogResultChannelMap: MutableMap<Dialog, Channel<InteractionResult>> =
        mutableMapOf()

    override val snackBarMessageChannel: Channel<SnackbarVisuals> = Channel()
    override val snackBarResultChannel: Channel<SnackbarResult> = Channel()

    override suspend fun showMessageDialogAndWaitResult(dialog: Dialog): InteractionResult {
        if (dialogResultChannelMap.containsKey(dialog)) {
            throw IllegalArgumentException("Dialog $dialog is already shown")
        }

        sendDialogChannel.trySend(dialog)

        try {
            val resultChannel = Channel<InteractionResult>(BUFFERED)
            dialogResultChannelMap[dialog] = resultChannel

            return resultChannel.receive()
        } catch (exception: CancellationException) {
            dialogResultChannelMap.remove(dialog)
            throw exception
        }
    }

    override suspend fun showSnackBarAndWaitResult(
        message: SnackBarMessage,
        messageFormatArgs: List<Any>
    ): SnackbarResult {
        snackBarMessageChannel.send(message.toSnackbarVisuals(messageFormatArgs))
        return snackBarResultChannel.receive()
    }

    override fun onResult(dialog: Dialog, result: InteractionResult) {
        if (!dialogResultChannelMap.containsKey(dialog)) {
            // dialog now shown or request canceled
            return
        }

        val channel = dialogResultChannelMap.remove(dialog)
        channel?.trySend(result)
    }

    override fun close() {
        sendDialogChannel.close()
        dialogResultChannelMap.values.forEach {
            it.close()
        }
    }
}
