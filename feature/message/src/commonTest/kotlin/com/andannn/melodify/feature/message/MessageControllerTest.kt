package com.andannn.melodify.feature.message

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageControllerTest {
    private val testScope = TestScope()
    private val messageController = MessageControllerImpl()

    @Test
    fun message_controller_test() = testScope.runTest {
        var dialog: MessageDialog? = null
        launch {
            dialog = messageController.sendDialogChannel.receive()

            messageController.onResult(dialog!!, InteractionResult.ACCEPT)
        }

        val result = messageController.showMessageDialogAndWaitResult(MessageDialog.ConfirmDeletePlaylist)

        assertEquals(MessageDialog.ConfirmDeletePlaylist, dialog)
        assertEquals(InteractionResult.ACCEPT, result)
    }

    @Test
    fun message_controller_cancel_test() = testScope.runTest {
        var dialog: MessageDialog? = null
        val job1 = launch(
            start = CoroutineStart.UNDISPATCHED
        ) {
            dialog = messageController.sendDialogChannel.receive()

            delay(10)

            messageController.onResult(dialog!!, InteractionResult.ACCEPT)
        }

        var result: InteractionResult? = null
        val job2 = launch(
            start = CoroutineStart.UNDISPATCHED
        ) {
            result = messageController.showMessageDialogAndWaitResult(MessageDialog.ConfirmDeletePlaylist)
        }
        job2.cancel()
        joinAll(job1, job2)

        assertEquals(MessageDialog.ConfirmDeletePlaylist, dialog)
        assertEquals(null, result)
    }
}