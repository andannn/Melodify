/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.util

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalDragOrCancellation
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach
import kotlin.math.absoluteValue
import kotlin.math.sign

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
 * @param onDrag Called when start vertical drag with the offset.
 * @param onDragEnd Called when the drag end.
 * @param onLongPressStart Called immediately when a long press is detected.
 * @param onLongPressEnd Called when the user releases the pointer after a long press.
 * @param onContinuousTap Called on the second tap and every subsequent tap in a rapid sequence.
 */
internal fun Modifier.detectLongPressAndContinuousTap(
    key: Any?,
    onTap: () -> Unit = {},
    onLongPressStart: () -> Unit = {},
    onLongPressEnd: () -> Unit = {},
    onDrag: (Float) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onContinuousTap: () -> Unit = {},
    onContinuousTapEnd: () -> Unit = {},
) = pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown()
        down.consume()

        val upOrCancel =
            when (val waitResult = waitForLongPressOrDrag(down.id)) {
                LongPressOrDragWaitResult.Success -> {
                    onLongPressStart()
                    consumeUntilUp()
                    onLongPressEnd()
                    // End the current gesture
                    return@awaitEachGesture
                }

                is LongPressOrDragWaitResult.DragVerticalTouchSlopReached -> {
                    // reach the touch slop, fire drag event
                    onDrag(waitResult.overSlop)

                    var change: PointerInputChange? = waitResult.change
                    while (change != null && change.pressed) {
                        change = awaitVerticalDragOrCancellation(change.id)
                        if (change != null && change.pressed) {
                            val offset = change.positionChange().y
                            onDrag(offset)
                            change.consume()
                        }
                    }
                    onDragEnd()
                    return@awaitEachGesture
                }

                is LongPressOrDragWaitResult.Released -> {
                    waitResult.finalUpChange
                }

                is LongPressOrDragWaitResult.Canceled -> {
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
                onContinuousTapEnd()
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

internal sealed class LongPressOrDragWaitResult {
    /** Long press was triggered */
    object Success : LongPressOrDragWaitResult()

    /** All pointers were released without long press being triggered */
    class Released(
        val finalUpChange: PointerInputChange,
    ) : LongPressOrDragWaitResult()

    /**
     * The vertical drag touch slop was reached,
     * @param overSlop is the distance beyond the pointer slop.
     * @param change is the pointer change when the touch slop was reached.
     */
    class DragVerticalTouchSlopReached(
        val overSlop: Float,
        val change: PointerInputChange,
    ) : LongPressOrDragWaitResult()

    /** The gesture was canceled */
    object Canceled : LongPressOrDragWaitResult()
}

internal suspend fun AwaitPointerEventScope.waitForLongPressOrDrag(
    downPointerId: PointerId,
    pass: PointerEventPass = PointerEventPass.Main,
): LongPressOrDragWaitResult {
    var result: LongPressOrDragWaitResult = LongPressOrDragWaitResult.Canceled
    val touchSlopDetector = TouchSlopDetector(Orientation.Vertical)
    val touchSlop = viewConfiguration.touchSlop

    try {
        withTimeout(viewConfiguration.longPressTimeoutMillis) {
            while (true) {
                val event = awaitPointerEvent(pass)
                if (event.changes.fastAll { it.changedToUp() }) {
                    // All pointers are up
                    result = LongPressOrDragWaitResult.Released(event.changes[0])
                    break
                }

                if (
                    event.changes.fastAny {
                        it.isConsumed || it.isOutOfBounds(size, extendedTouchPadding)
                    }
                ) {
                    result = LongPressOrDragWaitResult.Canceled
                    break
                }

                // Check for cancel by position consumption. We can look on the Final pass of the
                // existing pointer event because it comes after the pass we checked above.
                val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                if (consumeCheck.changes.fastAny { it.isConsumed }) {
                    result = LongPressOrDragWaitResult.Canceled
                    break
                }

                // Check reach touch slop
                val dragEvent =
                    event.changes.fastFirstOrNull { it.id == downPointerId } ?: break
                if (dragEvent.isConsumed) {
                    break
                } else {
                    val postSlopOffset =
                        touchSlopDetector.addPositions(
                            dragEvent.position,
                            dragEvent.previousPosition,
                            touchSlop,
                        )
                    if (postSlopOffset.isSpecified) {
                        dragEvent.consume()
                        result =
                            LongPressOrDragWaitResult.DragVerticalTouchSlopReached(
                                postSlopOffset.y,
                                dragEvent,
                            )
                        break
                    }
                }
            }
        }
    } catch (_: PointerEventTimeoutCancellationException) {
        return LongPressOrDragWaitResult.Success
    }
    return result
}

/**
 * Detects if touch slop has been crossed after adding a series of [PointerInputChange]. For every
 * new [PointerInputChange] one should add it to this detector using [addPositions]. If the position
 * change causes the touch slop to be crossed, [addPositions] will return true.
 */
internal class TouchSlopDetector(
    var orientation: Orientation? = null,
    initialPositionChange: Offset = Offset.Zero,
) {
    fun Offset.mainAxis() = if (orientation == Orientation.Horizontal) x else y

    fun Offset.crossAxis() = if (orientation == Orientation.Horizontal) y else x

    /** The accumulation of drag deltas in this detector. */
    private var totalPositionChange: Offset = initialPositionChange

    /**
     * Adds [dragEvent] to this detector. If the accumulated position changes crosses the touch slop
     * provided by [touchSlop], this method will return the post slop offset, that is the total
     * accumulated delta change minus the touch slop value, otherwise this should return null.
     */
    fun addPositions(
        currentPosition: Offset,
        previousPosition: Offset,
        touchSlop: Float,
    ): Offset {
        val positionChange = currentPosition - previousPosition
        totalPositionChange += positionChange

        val inDirection =
            if (orientation == null) {
                totalPositionChange.getDistance()
            } else {
                totalPositionChange.mainAxis().absoluteValue
            }

        val hasCrossedSlop = inDirection >= touchSlop

        return if (hasCrossedSlop) {
            calculatePostSlopOffset(touchSlop)
        } else {
            Offset.Unspecified
        }
    }

    /**
     * Resets the accumulator associated with this detector.
     *
     * @param initialPositionAccumulator Use to initialize the position change accumulator, for
     *   instance in cases where slop detection may happen "mid-gesture", that is, the slop
     *   detection didn't start from the first down event but somewhere after.
     */
    fun reset(initialPositionAccumulator: Offset = Offset.Zero) {
        totalPositionChange = initialPositionAccumulator
    }

    private fun calculatePostSlopOffset(touchSlop: Float): Offset =
        if (orientation == null) {
            val touchSlopOffset =
                totalPositionChange / totalPositionChange.getDistance() * touchSlop
            // update postSlopOffset
            totalPositionChange - touchSlopOffset
        } else {
            val finalMainAxisChange =
                totalPositionChange.mainAxis() - (sign(totalPositionChange.mainAxis()) * touchSlop)
            val finalCrossAxisChange = totalPositionChange.crossAxis()
            if (orientation == Orientation.Horizontal) {
                Offset(finalMainAxisChange, finalCrossAxisChange)
            } else {
                Offset(finalCrossAxisChange, finalMainAxisChange)
            }
        }
}
