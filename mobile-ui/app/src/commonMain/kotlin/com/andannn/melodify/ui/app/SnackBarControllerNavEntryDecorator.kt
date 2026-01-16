/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.NavEntryDecorator
import com.andannn.melodify.shared.compose.popup.snackbar.LocalSnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController

@Composable
internal fun <T : Any> rememberSnackBarControllerNavEntryDecorator(): NavEntryDecorator<T> =
    remember { SnackBarControllerNavEntryDecorator() }

private class SnackBarControllerNavEntryDecorator<T : Any> :
    NavEntryDecorator<T>(
        onPop = {
        },
        decorate = { entry ->
            val holder = retain { SnackBarController() }
            CompositionLocalProvider(
                LocalSnackBarController provides holder,
            ) {
                entry.Content()
            }
        },
    )
