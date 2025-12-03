/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import io.github.andannn.RetainedModel
import io.github.andannn.retainRetainedModel

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
