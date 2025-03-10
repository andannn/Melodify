package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Player(
    state: PlayerUiState,
    modifier: Modifier = Modifier
)