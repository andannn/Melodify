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
import melodify.composeapp.generated.resources.tab_already_exist
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

sealed class SnackBarMessage(
    private val duration: SnackbarDuration = SnackbarDuration.Short,
    private val message: StringResource,
    private val actionLabel: StringResource? = null,
    private val withDismissAction: Boolean = false,
) {
    data object AddPlayListSuccess : SnackBarMessage(
        message = Res.string.add_to_playlist_success_message,
    )

    data object AddPlayListFailed : SnackBarMessage(
        message = Res.string.add_to_playlist_failed_message,
    )

    data object TabAlreadyExist : SnackBarMessage(
        message = Res.string.tab_already_exist,
    )

    suspend fun toSnackbarVisuals(messageFormatArgs: List<Any>): SnackbarVisuals {
        val actionLabel = actionLabel?.let { getString(it) }
        val duration = duration
        val message = getString(message, *messageFormatArgs.toTypedArray())
        val withDismissAction = withDismissAction
        return object : SnackbarVisuals {
            override val actionLabel = actionLabel
            override val duration = duration
            override val message = message
            override val withDismissAction = withDismissAction
        }
    }
}
