/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.librarydetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.ui.components.librarydetail.LibraryContentEvent
import com.andannn.melodify.ui.components.librarydetail.rememberLibraryDetailPresenter
import com.andannn.melodify.ui.components.mediaitem.MediaLibraryItem
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.core.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.andannn.melodify.windows.CustomMenuBar
import com.andannn.melodify.windows.LaunchNavigationRequestHandlerEffect
import com.andannn.melodify.windows.WindowNavigator
import com.andannn.melodify.windows.handleMenuEvent

@Composable
fun LibraryDetailWindow(
    navigator: WindowNavigator,
    dataSource: LibraryDataSource,
    onCloseRequest: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    Window(
        onCloseRequest = onCloseRequest,
        title = title,
    ) {
        val presenter = rememberLibraryDetailPresenter(dataSource)
        val state = presenter.present()
        LaunchedEffect(state.title) {
            title = state.title
        }

        LaunchNavigationRequestHandlerEffect(
            eventSink = presenter as NavigationRequestEventSink,
            navigator = navigator,
        )

        CustomMenuBar(navigator::handleMenuEvent)

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = rememberAndSetupSnackBarHostState(),
                    modifier = Modifier.padding(bottom = 64.dp),
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.padding(it),
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 128.dp),
            ) {
                items(
                    items = state.contentList,
                    key = { it.id },
                ) { item ->
                    MediaLibraryItem(
                        modifier = Modifier.padding(vertical = 4.dp),
                        mediaItemModel = item,
                        playListId = (dataSource as? LibraryDataSource.PlayListDetail)?.id,
                        onItemClick = {
                            state.eventSink.invoke(
                                LibraryContentEvent.OnMediaItemClick(item),
                            )
                        },
                    )
                }
            }
        }

        ActionDialogContainer()
    }
}
