package com.andannn.melodify.feature.common.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun AndroidBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}