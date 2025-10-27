/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import com.andannn.melodify.model.asDataSource
import com.andannn.melodify.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.LibraryDetailScreen
import com.andannn.melodify.ui.components.common.MediaItemWithOptionAction
import com.andannn.melodify.ui.components.librarydetail.LibraryContentState
import com.andannn.melodify.ui.components.librarydetail.rememberLibraryDetailPresenter
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter

class LibraryDetailScreenPresenter(
    private val dataSource: LibraryDataSource,
    private val navigator: Navigator,
) : Presenter<LibraryDetailScreenState> {
    @Composable
    override fun present(): LibraryDetailScreenState {
        val state = rememberLibraryDetailPresenter(dataSource).present()
        return LibraryDetailScreenState(
            state,
        ) { event ->
            when (event) {
                LibraryDetailScreenEvent.OnBackKeyPressed -> navigator.pop()
                is LibraryDetailScreenEvent.OnMediaItemClick ->
                    navigator.goTo(
                        LibraryDetailScreen(event.mediaItem.asDataSource()),
                    )
            }
        }
    }
}

data class LibraryDetailScreenState(
    val state: LibraryContentState,
    val eventSink: (LibraryDetailScreenEvent) -> Unit,
) : CircuitUiState

sealed interface LibraryDetailScreenEvent {
    data object OnBackKeyPressed : LibraryDetailScreenEvent

    data class OnMediaItemClick(
        val mediaItem: MediaItemModel,
    ) : LibraryDetailScreenEvent
}

@Composable
fun LibraryDetail(
    screenState: LibraryDetailScreenState,
    modifier: Modifier = Modifier,
) {
    LibraryDetailContent(
        modifier = modifier,
        title = screenState.state.title,
        contentList = screenState.state.contentList,
        onBackPressed = {
            screenState.eventSink(LibraryDetailScreenEvent.OnBackKeyPressed)
        },
        onItemClick = {
            screenState.eventSink(LibraryDetailScreenEvent.OnMediaItemClick(it))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryDetailContent(
    title: String,
    contentList: List<MediaItemModel>,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onItemClick: (MediaItemModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(rememberAndSetupSnackBarHostState())
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
                MediaItemWithOptionAction(
                    modifier = Modifier.padding(vertical = 4.dp),
                    mediaItemModel = item,
                    onItemClick = {
                        onItemClick(item)
                    },
                )
            }
        }
    }

    ActionDialogContainer()
}
