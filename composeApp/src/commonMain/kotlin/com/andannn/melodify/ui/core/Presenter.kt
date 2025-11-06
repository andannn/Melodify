package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.RetainObserver
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

interface Presenter<UiState> {
    @Composable
    fun present(): UiState
}

abstract class ScopedPresenter<UiState> :
    Presenter<UiState>,
    ScopedObserver by ScopedObserverImpl()

interface ScopedObserver :
    RetainObserver,
    CoroutineScope

private class ScopedObserverImpl : ScopedObserver {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override fun onRetained() {}

    override fun onEnteredComposition() {}

    override fun onExitedComposition() {}

    override fun onRetired() {
        job.cancel()
    }

    override fun onUnused() {
        job.cancel()
    }
}
