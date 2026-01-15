/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import io.github.aakira.napier.Napier

val LocalSnackBarController: ProvidableCompositionLocal<SnackBarController> =
    compositionLocalOf { error("no SnackBarController") }

fun SnackBarController(): SnackBarController = SnackBarControllerImpl()

interface SnackBarController {
    var snackBarController: SnackbarHostState?

    suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult
}

@Composable
fun rememberAndSetupSnackBarHostState(holder: SnackBarController = LocalSnackBarController.current): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(snackbarHostState) {
        holder.snackBarController = snackbarHostState

        onDispose {
            holder.snackBarController = null
        }
    }

    return snackbarHostState
}

private const val TAG = "SnackBarController"

internal class SnackBarControllerImpl : SnackBarController {
    override var snackBarController: SnackbarHostState? = null

    /**
     * Show snackbar and wait for user interaction.
     */
    override suspend fun showSnackBar(message: SnackBarMessage): SnackbarResult {
        Napier.d(tag = TAG) { "show snackbar. message = $message" }
        return snackBarController
            ?.showSnackbar(message.toSnackbarVisuals())
            ?.also {
                Napier.d(tag = TAG) { "showSnackBar. result = $it" }
            }
            ?: error("Snackbar HostState is not setup. ")
    }
}
