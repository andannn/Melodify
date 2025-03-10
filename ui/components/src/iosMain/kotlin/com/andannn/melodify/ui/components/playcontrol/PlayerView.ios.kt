package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.ui.components.playcontrol.ui.PlayerAreaView

@Composable
actual fun Player(
    presenter: PlayerPresenter,
    modifier: Modifier
)  = PlayerAreaView(modifier, stateHolder)