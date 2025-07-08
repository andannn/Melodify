/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.screenshots

import androidx.compose.runtime.Composable
import com.andannn.melodify.screenshots.util.ScreenShotsTest
import com.andannn.melodify.screenshots.util.snapshotWithOption
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.library.Library
import com.andannn.melodify.ui.components.library.LibraryState
import org.junit.Test

@Composable
fun LibraryScreenShots(isDark: Boolean) {
    MelodifyTheme(isDark) {
        Library(
            state = LibraryState(),
        )
    }
}

class LibraryScreenShots : ScreenShotsTest() {
    @Test
    fun takeScreenShot() {
        paparazzi.snapshotWithOption("LibraryScreenShots") { isDark ->
            LibraryScreenShots(isDark)
        }
    }
}
