package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Player(
    modifier: Modifier = Modifier,
    stateHolder: PlayStateHolder = rememberPlayStateHolder()
)