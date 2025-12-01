/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.EmptyCoroutineContext

abstract class RetainedModel {
    val retainedScope: CoroutineScope = createViewModelScope()

    internal fun clear() {
        retainedScope.cancel()
        onClear()
    }

    open fun onClear() {}
}

internal fun createViewModelScope(): CoroutineScope {
    val dispatcher =
        try {
            Dispatchers.Main.immediate
        } catch (_: NotImplementedError) {
            EmptyCoroutineContext
        } catch (_: IllegalStateException) {
            EmptyCoroutineContext
        }
    return CoroutineScope(context = dispatcher + SupervisorJob())
}

@Composable
fun <T : RetainedModel> retainRetainedModel(
    vararg keys: Any?,
    factory: () -> T,
): T =
    retain(keys = keys) {
        RetainedModelObserver(factory())
    }.retainedModel

private class RetainedModelObserver<T : RetainedModel>(
    val retainedModel: T,
) : RetainObserver {
    override fun onRetained() {}

    override fun onEnteredComposition() {}

    override fun onExitedComposition() {}

    override fun onRetired() {
        retainedModel.clear()
    }

    override fun onUnused() {
        retainedModel.clear()
    }
}
