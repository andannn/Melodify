package com.andannn.melodify.ui.common.widgets

import androidx.compose.runtime.Composable

@Composable
expect fun AndroidBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
