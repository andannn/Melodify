package com.andannn.melodify.ui.player.internal.util

import kotlin.test.Test
import kotlin.test.assertEquals

class OffsetToStepHelperTest {
    @Test
    fun stepThresholdTest() {
        var positiveCall = 0
        var negativeCall = 0
        val helper =
            OffsetToStepHelper(stepThreshold = 10f, onStep = {
                if (it) positiveCall++ else negativeCall++
            })
        helper.onOffset(9f)
        assertEquals(0, positiveCall)
        assertEquals(0, negativeCall)
        helper.onOffset(1f)
        assertEquals(1, positiveCall)
        assertEquals(0, negativeCall)
    }

    @Test
    fun negativeThresholdTest() {
        var positiveCall = 0
        var negativeCall = 0
        val helper =
            OffsetToStepHelper(stepThreshold = 10f, onStep = {
                if (it) positiveCall++ else negativeCall++
            })
        helper.onOffset(-19f)
        assertEquals(0, positiveCall)
        assertEquals(1, negativeCall)
        helper.onOffset(-1f)
        assertEquals(0, positiveCall)
        assertEquals(2, negativeCall)
    }

    @Test
    fun acculemateTest() {
        var positiveCall = 0
        var negativeCall = 0
        val helper =
            OffsetToStepHelper(stepThreshold = 10f, onStep = {
                if (it) positiveCall++ else negativeCall++
            })
        helper.onOffset(15f)
        assertEquals(1, positiveCall)
        assertEquals(0, negativeCall)
        helper.onOffset(5f)
        assertEquals(2, positiveCall)
        assertEquals(0, negativeCall)
    }

    @Test
    fun resetWhenChangeDirectionTest() {
        var positiveCall = 0
        var negativeCall = 0
        val helper =
            OffsetToStepHelper(stepThreshold = 10f, onStep = {
                if (it) positiveCall++ else negativeCall++
            })
        helper.onOffset(25f)
        assertEquals(2, positiveCall)
        assertEquals(0, negativeCall)
        helper.onOffset(-5f)
        assertEquals(2, positiveCall)
        assertEquals(0, negativeCall)
        helper.onOffset(-5f)
        assertEquals(2, positiveCall)
        assertEquals(1, negativeCall)
    }
}
