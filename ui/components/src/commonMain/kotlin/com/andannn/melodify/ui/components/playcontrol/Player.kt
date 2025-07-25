/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.ui.components.playcontrol.ui.PlayerViewContent

@Composable
fun Player(
    presenter: PlayerPresenter = rememberPlayerPresenter(),
    modifier: Modifier = Modifier,
) {
    PlayerAreaView(
        state = presenter.present(),
        modifier = modifier,
    )
}

@Composable
fun PlayerAreaView(
    state: PlayerUiState,
    modifier: Modifier = Modifier,
) {
    if (state is PlayerUiState.Active) {
        PlayerViewContent(
            state = state,
            onEvent = {
                state.eventSink.invoke(it)
            },
            modifier = modifier,
        )
    }
}
