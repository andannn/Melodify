package com.andannn.melodify.screenshots

import androidx.compose.runtime.Composable
import com.andannn.melodify.screenshots.util.ScreenShotsTest
import com.andannn.melodify.screenshots.util.audioList1
import com.andannn.melodify.screenshots.util.snapshotWithOption
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContent
import com.andannn.melodify.ui.components.librarycontentlist.LibraryContentState
import org.junit.Test

@Composable
fun LibraryContentScreenShots(isDark: Boolean) {
    MelodifyTheme(isDark) {
        LibraryContent(
            state = LibraryContentState(
                title = "Content List title",
                contentList = audioList1
            )
        )
    }
}

class LibraryContentScreenShots : ScreenShotsTest() {
    @Test
    fun takeScreenShot() {
        paparazzi.snapshotWithOption("LibraryContentScreenShots") { isDark ->
            LibraryContentScreenShots(isDark)
        }
    }
}