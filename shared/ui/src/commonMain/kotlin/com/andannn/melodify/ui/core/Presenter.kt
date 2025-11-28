/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

interface Presenter<UiState> {
    @Composable
    fun present(): UiState
}

abstract class RetainedPresenter<UiState> :
    RetainedModel(),
    Presenter<UiState>

@Composable
fun <T> retainPresenter(
    vararg keys: Any?,
    factory: () -> RetainedPresenter<T>,
): Presenter<T> =
    retainRetainedModel(
        keys = keys,
        factory = factory,
    )
