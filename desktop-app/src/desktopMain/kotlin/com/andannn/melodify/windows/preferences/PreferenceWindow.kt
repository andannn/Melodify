/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.popup.dialog.ActionDialog
import com.andannn.melodify.windows.CustomMenuBar
import com.andannn.melodify.windows.WindowNavigator
import com.andannn.melodify.windows.handleMenuEvent

@Composable
internal fun PreferenceWindow(
    navigator: WindowNavigator,
    onCloseRequest: () -> Unit,
) {
    Window(
        state = rememberWindowState(),
        onCloseRequest = onCloseRequest,
        title = "Preferences",
    ) {
        CustomMenuBar(navigator::handleMenuEvent)

        PreferencesWindowContent()

        ActionDialog()
    }
}

@Composable
private fun PreferencesWindowContent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
    ) {
        LibraryPreference(
            modifier = Modifier.fillMaxHeight().weight(2f),
        )
    }
}

@Composable
fun LibraryPreference(
    presenter: Presenter<LibraryPreferenceUiState> = retainLibraryPreferenceState(),
    modifier: Modifier = Modifier,
) {
    val state = presenter.present()
    Column(
        modifier = modifier,
    ) {
        Spacer(Modifier.width(8.dp))

        Row(
            verticalAlignment = CenterVertically,
        ) {
            Spacer(Modifier.width(16.dp))
            Text(
                "Library path",
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    state.eventSink.invoke(LibraryPreferenceUiEvent.OnAddLibraryButtonClick)
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Edit",
                )
            }

            Spacer(Modifier.width(16.dp))
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            state.libraryPath.forEach {
                LibraryPathItem(
                    path = it,
                    onDelete = {
                        state.eventSink.invoke(LibraryPreferenceUiEvent.OnDeleteLibraryPath(it))
                    },
                )
                HorizontalDivider(modifier.padding(horizontal = 4.dp))
            }
        }
    }
}

@Composable
fun LibraryPathItem(
    modifier: Modifier = Modifier,
    path: String,
    onDelete: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically,
    ) {
        Text(path)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onDelete,
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Edit",
            )
        }
    }
}
