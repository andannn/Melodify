/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.cover

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach

/**
 * Detects long presses, single taps, and continuous rapid taps.
 *
 * This modifier handles gesture events with the following logic:
 *
 * **Long Press:**
 * * [onLongPressStart] is called when a long press is detected.
 * * [onLongPressEnd] is called when the long press is released.
 *
 * **Tapping:**
 * * If a second tap is detected within [androidx.compose.ui.platform.ViewConfiguration.doubleTapTimeoutMillis],
 * [onContinuousTap] is called for that second tap and every subsequent rapid tap.
 * * If no second tap is detected within the timeout, [onTap] is called when the press is released.
 *
 * @param key The key used to cancel and restart the pointer input block.
 * @param onTap Called when a single tap is confirmed (no subsequent tap followed).
 * @param onLongPressStart Called immediately when a long press is detected.
 * @param onLongPressEnd Called when the user releases the pointer after a long press.
 * @param onContinuousTap Called on the second tap and every subsequent tap in a rapid sequence.
 */
internal fun Modifier.detectLongPressAndContinuousTap(
    key: Any?,
    onTap: () -> Unit = {},
    onLongPressStart: () -> Unit = {},
    onLongPressEnd: () -> Unit = {},
    onContinuousTap: () -> Unit = {},
) = pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown()
        down.consume()

        val upOrCancel =
            when (val longPressResult = waitForLongPress()) {
                LongPressResult.Success -> {
                    onLongPressStart()
                    consumeUntilUp()
                    onLongPressEnd()
                    // End the current gesture
                    return@awaitEachGesture
                }

                is LongPressResult.Released -> {
                    longPressResult.finalUpChange
                }

                is LongPressResult.Canceled -> {
                    null
                }
            }

        upOrCancel?.consume()
        if (upOrCancel != null) {
            // tap was successful.
            val secondDown = awaitSecondDown(upOrCancel)
            if (secondDown == null) {
                onTap()
            } else {
                secondDown.consume()
                // Double tap detected.

                var upOrNull: PointerInputChange?
                do {
                    upOrNull =
                        withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
                            waitForUpOrCancellation()
                        }
                    if (upOrNull != null) {
                        onContinuousTap()
                    }
                } while (upOrNull != null)
            }
        }
    }
}

/**
 * The following code copy from:
 * compose/foundation/foundation/src/commonMain/androidx/compose/foundation/gestures/TapGestureDetector.kt
 */

private suspend fun AwaitPointerEventScope.awaitSecondDown(firstUp: PointerInputChange): PointerInputChange? =
    withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
        val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
        var change: PointerInputChange
        // The second tap doesn't count if it happens before DoubleTapMinTime of the first tap
        do {
            change = awaitFirstDown()
        } while (change.uptimeMillis < minUptime)
        change
    }

private suspend fun AwaitPointerEventScope.consumeUntilUp() {
    do {
        val event = awaitPointerEvent()
        event.changes.fastForEach { it.consume() }
    } while (event.changes.fastAny { it.pressed })
}

internal sealed class LongPressResult {
    /** Long press was triggered */
    object Success : LongPressResult()

    /** All pointers were released without long press being triggered */
    class Released(
        val finalUpChange: PointerInputChange,
    ) : LongPressResult()

    /** The gesture was canceled */
    object Canceled : LongPressResult()
}

internal suspend fun AwaitPointerEventScope.waitForLongPress(pass: PointerEventPass = PointerEventPass.Main): LongPressResult {
    var result: LongPressResult = LongPressResult.Canceled
    try {
        withTimeout(viewConfiguration.longPressTimeoutMillis) {
            while (true) {
                val event = awaitPointerEvent(pass)
                if (event.changes.fastAll { it.changedToUp() }) {
                    // All pointers are up
                    result = LongPressResult.Released(event.changes[0])
                    break
                }

                if (
                    event.changes.fastAny {
                        it.isConsumed || it.isOutOfBounds(size, extendedTouchPadding)
                    }
                ) {
                    result = LongPressResult.Canceled
                    break
                }

                // Check for cancel by position consumption. We can look on the Final pass of the
                // existing pointer event because it comes after the pass we checked above.
                val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                if (consumeCheck.changes.fastAny { it.isConsumed }) {
                    result = LongPressResult.Canceled
                    break
                }
            }
        }
    } catch (_: PointerEventTimeoutCancellationException) {
        return LongPressResult.Success
    }
    return result
}
