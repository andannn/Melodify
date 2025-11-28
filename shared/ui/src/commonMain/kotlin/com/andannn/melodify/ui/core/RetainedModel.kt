package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class RetainedModel {
    val retainedScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    internal fun clear() {
        retainedScope.cancel()
        onClear()
    }

    fun onClear() {}
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
