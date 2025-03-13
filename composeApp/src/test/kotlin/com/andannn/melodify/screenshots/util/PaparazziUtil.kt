package com.andannn.melodify.screenshots.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.NoOpPopupController
import org.junit.Rule

fun Paparazzi.snapshotWithOption(name: String, composable: @Composable (isDark: Boolean) -> Unit) {
    @Composable
    fun provideDummyDependency(content : @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
            LocalRepository provides remember { Repository() },
            LocalPopupController provides remember { NoOpPopupController() },
        ) {
            content()
        }
    }

    snapshot("${name}_light") {
        provideDummyDependency {
            composable(false)
        }
    }
    snapshot("${name}_dark") {
        provideDummyDependency {
            composable(true)
        }
    }
}

abstract class ScreenShotsTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5,
    )
}