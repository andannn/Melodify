/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.model.browseable
import com.andannn.melodify.ui.components.mediaitem.MediaLibraryItem
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer

@Composable
fun LibraryDetail(
    dataSource: LibraryDataSource,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    presenter: Presenter<LibraryDetailScreenState> =
        rememberLibraryDetailScreenPresenter(
            dataSource = dataSource,
            navigator = navigator,
        ),
) {
    val screenState = presenter.present()
    LibraryDetailContent(
        modifier = modifier,
        title = screenState.state.title,
        dataSource = screenState.state.dataSource,
        contentList = screenState.state.contentList,
        onBackPressed = {
            screenState.eventSink(LibraryDetailScreenEvent.OnBackKeyPressed)
        },
        onItemClick = {
            screenState.eventSink(LibraryDetailScreenEvent.OnMediaItemClick(it))
        },
        onOpenMenuClick = {
            screenState.eventSink(LibraryDetailScreenEvent.OnOptionClick)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryDetailContent(
    title: String,
    contentList: List<MediaItemModel>,
    dataSource: LibraryDataSource,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onItemClick: (MediaItemModel) -> Unit = {},
    onOpenMenuClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 64.dp),
                hostState = rememberAndSetupSnackBarHostState(),
            )
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text(text = title)
                },
                actions = {
                    if (!dataSource.browseable()) {
                        IconButton(
                            onClick = onOpenMenuClick,
                        ) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "menu")
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                },
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 128.dp),
        ) {
            items(
                items = contentList,
                key = { it.id },
            ) { item ->
                MediaLibraryItem(
                    modifier = Modifier.padding(vertical = 4.dp),
                    mediaItemModel = item,
                    playListId = (dataSource as? LibraryDataSource.PlayListDetail)?.id,
                    onItemClick = {
                        onItemClick(item)
                    },
                )
            }
        }
    }

    Player()
    ActionDialogContainer()
}
