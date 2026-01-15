/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.common

import androidx.compose.runtime.Composable
import kotlin.reflect.KClass

inline fun <T : Any> entryProvider(builder: DialogEntryProviderScope<T>.() -> Unit): (T) -> DialogEntry<T> =
    DialogEntryProviderScope<T>().apply(builder).build()

class DialogEntryProviderScope<T : Any> {
    private val providers = mutableMapOf<Any, EntryProvider<out T>>()
    private val clazzProviders = mutableMapOf<KClass<out T>, EntryClassProvider<out T>>()

    inline fun <reified K : T> entry(
        dialogType: DialogType,
        noinline content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
    ) {
        addEntryProvider(K::class, dialogType, content)
    }

    fun <K : T> DialogEntryProviderScope<T>.entry(
        dialogId: K,
        dialogType: DialogType,
        content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
    ) {
        addEntryProvider(dialogId, dialogType, content)
    }

    fun <K : T> addEntryProvider(
        dialogId: K,
        dialogType: DialogType,
        content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
    ) {
        require(dialogId !in providers) {
            "An `entry` with the key `key` has already been added: $dialogId."
        }
        providers[dialogId] = EntryProvider(dialogId, dialogType, content)
    }

    fun <K : T> addEntryProvider(
        clazz: KClass<out K>,
        dialogType: DialogType,
        content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
    ) {
        require(clazz !in clazzProviders) {
            "An `entry` with the same `clazz` has already been added: ${clazz.simpleName}."
        }
        clazzProviders[clazz] = EntryClassProvider(clazz, dialogType, content)
    }

    /**
     * Returns an instance of entryProvider created from the entry providers set on this builder.
     */
    @Suppress("UNCHECKED_CAST")
    @PublishedApi
    internal fun build(): (T) -> DialogEntry<T> =
        { key ->
            val entryClassProvider = clazzProviders[key::class] as? EntryClassProvider<T>
            val entryProvider = providers[key] as? EntryProvider<T>

            entryClassProvider?.run { DialogEntry(key, dialogType, content) }
                ?: entryProvider?.run { DialogEntry(dialogId, dialogType, content) }
                ?: error("no provider")
        }
}

private data class EntryClassProvider<K : Any>(
    val clazz: KClass<K>,
    val dialogType: DialogType,
    val content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
)

private data class EntryProvider<K : Any>(
    val dialogId: K,
    val dialogType: DialogType,
    val content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
)
