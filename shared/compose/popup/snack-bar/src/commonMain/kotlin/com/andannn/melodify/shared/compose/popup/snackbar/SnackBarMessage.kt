/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.add_to_playlist_success_message
import melodify.shared.compose.resource.generated.resources.added_to_play_next
import melodify.shared.compose.resource.generated.resources.added_to_play_queue
import melodify.shared.compose.resource.generated.resources.delete_failed
import melodify.shared.compose.resource.generated.resources.multiple_deleted
import melodify.shared.compose.resource.generated.resources.one_deleted
import melodify.shared.compose.resource.generated.resources.tab_already_exist
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

    data object TabAlreadyExist : SnackBarMessage(
        message = Res.string.tab_already_exist,
    )

    data class MultipleDeleteSuccess(
        val num: Int,
    ) : SnackBarMessage(message = Res.string.multiple_deleted) {
        override fun getArgs(): List<Any> = listOf(num)
    }

    data object OneDeleteSuccess : SnackBarMessage(
        message = Res.string.one_deleted,
    )

    data object AddedToPlayNext : SnackBarMessage(
        message = Res.string.added_to_play_next,
    )

    data object AddedToPlayQueue : SnackBarMessage(
        message = Res.string.added_to_play_queue,
    )

    data object DeleteFailed : SnackBarMessage(
        message = Res.string.delete_failed,
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
