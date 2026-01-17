/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import kotlin.reflect.KClass

@Immutable
class PopupEntry<T : PopupId<*>>(
    private val popupId: T,
    val metadata: Map<String, Any> = emptyMap(),
    private val content: @Composable (dialogId: T, onAction: (Any) -> Unit) -> Unit,
) {
    @Composable
    fun Content(onAction: (Any) -> Unit) {
        content(popupId, onAction)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PopupEntry<*>

        return popupId == other.popupId &&
            metadata == other.metadata &&
            content === other.content
    }

    override fun hashCode(): Int =
        popupId.hashCode() * 31 +
            metadata.hashCode() * 31 +
            content.hashCode() * 31

    override fun toString(): String = "PopupEntry(popupId=$popupId, metadata=$metadata, content=$content)"
}

inline fun <T : PopupId<*>> entryProvider(builder: PopupEntryProviderScope<T>.() -> Unit): (T) -> PopupEntry<T> =
    PopupEntryProviderScope<T>().apply(builder).build()

class PopupEntryProviderScope<T : PopupId<*>> {
    private val providers = mutableMapOf<Any, EntryProvider<out T>>()
    private val clazzProviders = mutableMapOf<KClass<out T>, EntryClassProvider<out T>>()

    inline fun <reified T : PopupId<R>, reified R : Any> PopupEntryProviderScope<PopupId<*>>.entry(
        metadata: Map<String, Any>,
        noinline content: @Composable (dialogId: T, onAction: (R) -> Unit) -> Unit,
    ) {
        addEntryProvider(T::class, metadata, content)
    }

    inline fun <reified T : PopupId<R>, reified R : Any> PopupEntryProviderScope<PopupId<*>>.entry(
        dialogId: T,
        metadata: Map<String, Any>,
        noinline content: @Composable (dialogId: T, onAction: (R) -> Unit) -> Unit,
    ) {
        addEntryProvider(dialogId, metadata, content)
    }

    fun <K : T> addEntryProvider(
        dialogId: K,
        metadata: Map<String, Any>,
        content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
    ) {
        require(dialogId !in providers) {
            "An `entry` with the key `key` has already been added: $dialogId."
        }
        providers[dialogId] = EntryProvider(dialogId, metadata, content)
    }

    fun <K : T> addEntryProvider(
        clazz: KClass<out K>,
        metadata: Map<String, Any>,
        content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
    ) {
        require(clazz !in clazzProviders) {
            "An `entry` with the same `clazz` has already been added: ${clazz.simpleName}."
        }
        clazzProviders[clazz] = EntryClassProvider(clazz, metadata, content)
    }

    /**
     * Returns an instance of entryProvider created from the entry providers set on this builder.
     */
    @Suppress("UNCHECKED_CAST")
    @PublishedApi
    internal fun build(): (T) -> PopupEntry<T> =
        { key ->
            val entryClassProvider = clazzProviders[key::class] as? EntryClassProvider<T>
            val entryProvider = providers[key] as? EntryProvider<T>

            entryClassProvider?.run { PopupEntry(key, metadata, content) }
                ?: entryProvider?.run { PopupEntry(dialogId, metadata, content) }
                ?: error("no provider")
        }
}

private data class EntryClassProvider<K : Any>(
    val clazz: KClass<K>,
    val metadata: Map<String, Any> = emptyMap(),
    val content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
)

private data class EntryProvider<K : Any>(
    val dialogId: K,
    val metadata: Map<String, Any> = emptyMap(),
    val content: @Composable (dialogId: K, onAction: (Any) -> Unit) -> Unit,
)
