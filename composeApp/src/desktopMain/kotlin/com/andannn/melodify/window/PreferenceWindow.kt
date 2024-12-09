package com.andannn.melodify.window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.andannn.melodify.MelodifyDesktopAppState
import com.andannn.melodify.ui.components.popup.dialog.ActionDialogContainer
import com.andannn.melodify.ui.components.tabselector.SelectableNavigationDrawerItem

@Composable
fun PreferenceWindow(
    appState: MelodifyDesktopAppState,
    onCloseRequest: () -> Unit
) {
    Window(
        state = rememberWindowState(),
        onCloseRequest = onCloseRequest,
        title = "Preferences",
    ) {
        CustomMenuBar(appState)

        PreferencesWindowContent()

        ActionDialogContainer()
    }
}

@Composable
private fun PreferencesWindowContent(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            SelectableNavigationDrawerItem(
                label = "Library",
                selected = false,
                onClick = {}
            )
        }

        VerticalDivider()

        LibraryPreference(
            modifier = Modifier.fillMaxHeight().weight(2f)
        )

    }
}

@Composable
fun LibraryPreference(
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(Modifier.width(8.dp))

        Row(
            verticalAlignment = CenterVertically
        ) {
            Spacer(Modifier.width(16.dp))
            Text(
                "Library path",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Edit"
                )
            }

            Spacer(Modifier.width(16.dp))
        }

        Surface(
            modifier = Modifier.weight(1f).padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.inverseSurface),
        ) {
        }
    }
}