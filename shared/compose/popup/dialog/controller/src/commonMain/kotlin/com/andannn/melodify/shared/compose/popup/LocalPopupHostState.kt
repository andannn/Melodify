package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalPopupHostState: ProvidableCompositionLocal<PopupHostState> =
    compositionLocalOf { error("No popup controller") }
