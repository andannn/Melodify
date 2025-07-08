/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun rememberMelodifyDeskTopAppState() =
    remember {
        MelodifyDeskTopAppState()
    }

internal class MelodifyDeskTopAppState {
    var showPreferenceWindow by mutableStateOf(false)

    fun closePreferenceWindow() {
        showPreferenceWindow = false
    }
}
