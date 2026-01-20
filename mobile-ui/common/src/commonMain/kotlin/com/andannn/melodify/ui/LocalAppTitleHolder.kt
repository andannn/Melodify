package com.andannn.melodify.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

data class AppTitleHolder(
    val title: String,
)

val LocalAppTitleHolder: ProvidableCompositionLocal<AppTitleHolder> =
    staticCompositionLocalOf {
        error("AppTitleHolder not provided")
    }
