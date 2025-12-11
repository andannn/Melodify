/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiState
import com.andannn.melodify.shared.compose.components.play.control.rememberPlayerPresenter

@Composable
internal fun Player(
    modifier: Modifier = Modifier,
    presenter: Presenter<PlayerUiState> = rememberPlayerPresenter(),
) {
    val state = presenter.present()
    if (state is PlayerUiState.Active) {
        PlayerView(
            state = state,
            onEvent = {
                state.eventSink.invoke(it)
            },
            modifier = modifier,
        )
    }
}
