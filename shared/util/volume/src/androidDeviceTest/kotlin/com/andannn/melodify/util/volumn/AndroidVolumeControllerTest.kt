/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.volumn

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AndroidVolumeControllerTest {
    private lateinit var controller: AndroidVolumeController

    @Before
    fun setUp() {
        controller = AndroidVolumeController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun setVolumeTest() {
        controller.setVolume(10)
        assertEquals(minOf(10, controller.getMaxVolume()), controller.getCurrentVolume())
    }
}
