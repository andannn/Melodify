package com.andannn.melodify.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.slack.circuit.retained.produceRetainedState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> Flow<T>.collectAsRetainedStateWithLifecycle(
    initialValue: T,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> =
    collectAsRetainedStateWithLifecycle(
        initialValue = initialValue,
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = minActiveState,
        context = context,
    )

@Composable
fun <T> Flow<T>.collectAsRetainedStateWithLifecycle(
    initialValue: T,
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> =
    produceRetainedState(initialValue, this, lifecycle, minActiveState, context) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                this@collectAsRetainedStateWithLifecycle.collect {
                    this@produceRetainedState.value = it
                }
            } else {
                withContext(context) {
                    this@collectAsRetainedStateWithLifecycle.collect {
                        this@produceRetainedState.value = it
                    }
                }
            }
        }
    }
