/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.add_to_playlist_failed_message
import melodify.composeapp.generated.resources.add_to_playlist_success_message
import melodify.composeapp.generated.resources.multiple_deleted
import melodify.composeapp.generated.resources.one_deleted
import melodify.composeapp.generated.resources.sync_completed
import melodify.composeapp.generated.resources.sync_failed
import melodify.composeapp.generated.resources.sync_progress
import melodify.composeapp.generated.resources.sync_start
import melodify.composeapp.generated.resources.tab_already_exist
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

sealed class SnackBarMessage(
    private val duration: SnackbarDuration = SnackbarDuration.Short,
    private val message: StringResource,
    private val actionLabel: StringResource? = null,
    private val withDismissAction: Boolean = false,
) {
    open fun getArgs(): List<Any> = emptyList()

    data class AddPlayListSuccess(
        val playListName: String,
    ) : SnackBarMessage(message = Res.string.add_to_playlist_success_message) {
        override fun getArgs(): List<Any> = listOf(playListName)
    }

    data object AddPlayListFailed : SnackBarMessage(
        message = Res.string.add_to_playlist_failed_message,
    )

    data object TabAlreadyExist : SnackBarMessage(
        message = Res.string.tab_already_exist,
    )

    data object SyncStatusStart : SnackBarMessage(
        message = Res.string.sync_start,
    )

    data class SyncProgress(
        val info: String,
    ) : SnackBarMessage(message = Res.string.sync_progress) {
        override fun getArgs(): List<Any> = listOf(info)
    }

    data class SyncCompleted(
        val num: Int,
    ) : SnackBarMessage(message = Res.string.sync_completed) {
        override fun getArgs(): List<Any> = listOf(num)
    }

    data object SyncFailed : SnackBarMessage(
        message = Res.string.sync_failed,
    )

    data class MultipleDeleteSuccess(
        val num: Int,
    ) : SnackBarMessage(message = Res.string.multiple_deleted) {
        override fun getArgs(): List<Any> = listOf(num)
    }

    data object OneDeleteSuccess : SnackBarMessage(
        message = Res.string.one_deleted,
    )

    suspend fun toSnackbarVisuals(): SnackbarVisuals {
        val actionLabel = actionLabel?.let { getString(it) }
        val duration = duration
        val message = getString(message, *getArgs().toTypedArray())
        val withDismissAction = withDismissAction
        return object : SnackbarVisuals {
            override val actionLabel = actionLabel
            override val duration = duration
            override val message = message
            override val withDismissAction = withDismissAction
        }
    }
}
