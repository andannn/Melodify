package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.RetainedValuesStoreRegistry
import androidx.compose.runtime.retain.retainRetainedValuesStoreRegistry
import androidx.navigation3.runtime.NavEntryDecorator
import io.github.aakira.napier.Napier

@Composable
fun <T : Any> rememberRetainedValueStoreNavEntryDecorator(
    retainedValuesStoreRegistry: RetainedValuesStoreRegistry = retainRetainedValuesStoreRegistry(),
): NavEntryDecorator<T> =
    remember(
        retainedValuesStoreRegistry,
    ) { RetainedValueStoreNavEntryDecorator(retainedValuesStoreRegistry) }

private class RetainedValueStoreNavEntryDecorator<T : Any>(
    private val retainedValuesStoreRegistry: RetainedValuesStoreRegistry,
) : NavEntryDecorator<T>(
        onPop = {
            retainedValuesStoreRegistry.clearChild(it)
        },
        decorate = { entry ->
            retainedValuesStoreRegistry.ProvideChildRetainedValuesStore(
                entry.contentKey,
            ) {
                entry.Content()
            }
        },
    )
