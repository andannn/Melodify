/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import com.andannn.melodify.ui.player.internal.Player

@Composable
fun <T : Any> rememberPlayerProviderNavEntryDecorator(): NavEntryDecorator<T> = remember { PlayerProviderNavEntryDecorator() }

private class PlayerProviderNavEntryDecorator<T : Any> :
    NavEntryDecorator<T>(
        onPop = {
        },
        decorate = { entry ->
            entry.Content()

            Player()
        },
    )
