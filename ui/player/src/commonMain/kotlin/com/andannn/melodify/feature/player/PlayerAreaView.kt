package com.andannn.melodify.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.ui.player.ui.PlayerView

@Composable
fun PlayerAreaView(
    state: PlayerUiState.Active,
    modifier: Modifier = Modifier,
    onEvent: (PlayerUiEvent) -> Unit,
) {
    PlayerView(
        state = state,
        onEvent = onEvent,
        modifier = modifier
    )
}