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

interface RetainedScopeOwner {
    val retainedScope: CoroutineScope

    fun onClear() = {}
}

abstract class ScopedPresenter<UiState> :
    Presenter<UiState>,
    RetainedScopeOwner {
    override val retainedScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}

@Composable
inline fun <reified T> retainPresenter(
    vararg keys: Any?,
    noinline factory: () -> ScopedPresenter<T>,
): Presenter<T> =
    retain(keys = keys) {
        val presenter = factory()
        Napier.d(tag = "Presenter") { "presenter init $presenter" }
        object : RetainObserver, Presenter<T> {
            override fun onRetained() {}

            override fun onEnteredComposition() {}

            override fun onExitedComposition() {}

            override fun onRetired() {
                Napier.d(tag = "Presenter") { "presenter close $presenter" }
                presenter.retainedScope.cancel()
                presenter.onClear()
            }

            override fun onUnused() {
                Napier.d(tag = "Presenter") { "presenter close $presenter" }
                presenter.retainedScope.cancel()
                presenter.onClear()
            }

            @Composable
            override fun present(): T = presenter.present()
        }
    }
