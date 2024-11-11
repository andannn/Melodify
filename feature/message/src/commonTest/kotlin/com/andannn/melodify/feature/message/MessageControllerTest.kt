package com.andannn.melodify.feature.message

import com.andannn.melodify.feature.message.dialog.Dialog
import com.andannn.melodify.feature.message.dialog.InteractionResult
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
        var dialog: Dialog? = null
        launch {
            dialog = messageController.sendDialogChannel.receive()

            messageController.onResult(dialog!!, InteractionResult.AlertDialog.ACCEPT)
        }

        val result = messageController.showMessageDialogAndWaitResult(Dialog.ConfirmDeletePlaylist)

        assertEquals(Dialog.ConfirmDeletePlaylist, dialog)
        assertEquals(InteractionResult.AlertDialog.ACCEPT, result)
    }

    @Test
    fun message_controller_cancel_test() = testScope.runTest {
        var dialog: Dialog? = null
        val job1 = launch(
            start = CoroutineStart.UNDISPATCHED
        ) {
            dialog = messageController.sendDialogChannel.receive()

            delay(10)

            messageController.onResult(dialog!!, InteractionResult.AlertDialog.ACCEPT)
        }

        var result: InteractionResult? = null
        val job2 = launch(
            start = CoroutineStart.UNDISPATCHED
        ) {
            result = messageController.showMessageDialogAndWaitResult(Dialog.ConfirmDeletePlaylist)
        }
        job2.cancel()
        joinAll(job1, job2)

        assertEquals(Dialog.ConfirmDeletePlaylist, dialog)
        assertEquals(null, result)
    }
}