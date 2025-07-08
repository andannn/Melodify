package com.andannn.melodify.ui.components.popup.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.add_to_playlist_failed_message
import melodify.ui.common.generated.resources.add_to_playlist_success_message
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
