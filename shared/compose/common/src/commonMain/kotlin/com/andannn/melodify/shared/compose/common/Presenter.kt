/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common

import androidx.compose.runtime.Composable
import io.github.andannn.RetainedModel
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

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

context(retainedModel: RetainedModel)
fun <T> Flow<T>.stateInRetainedModel(
    initialValue: T,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000),
): StateFlow<T> = stateIn(retainedModel.retainedScope, started, initialValue)
