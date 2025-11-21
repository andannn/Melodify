/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.RetainedValuesStoreRegistry
import androidx.compose.runtime.retain.retainRetainedValuesStoreRegistry
import androidx.navigation3.runtime.NavEntryDecorator

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
