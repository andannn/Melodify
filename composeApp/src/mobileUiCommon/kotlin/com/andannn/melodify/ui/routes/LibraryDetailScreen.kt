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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.model.asLibraryDataSource
import com.andannn.melodify.model.browseable
import com.andannn.melodify.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.LibraryDetailScreen
import com.andannn.melodify.ui.components.librarydetail.LibraryContentEvent
import com.andannn.melodify.ui.components.librarydetail.LibraryContentState
import com.andannn.melodify.ui.components.librarydetail.item.MediaLibraryItem
import com.andannn.melodify.ui.components.librarydetail.rememberLibraryDetailPresenter
import com.andannn.melodify.ui.components.librarydetail.showLibraryMediaOption
import com.andannn.melodify.ui.components.playcontrol.Player
import com.andannn.melodify.ui.popup.dialog.ActionDialogContainer
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.launch

@Composable
fun rememberLibraryDetailScreenPresenter(
    dataSource: LibraryDataSource,
    navigator: Navigator,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
): Presenter<LibraryDetailScreenState> =
    remember(
        dataSource,
        navigator,
        repository,
        popupController,
    ) {
        LibraryDetailScreenPresenter(dataSource, navigator, repository, popupController)
    }

private class LibraryDetailScreenPresenter(
    private val dataSource: LibraryDataSource,
    private val navigator: Navigator,
    private val repository: Repository,
    private val popupController: PopupController,
) : Presenter<LibraryDetailScreenState> {
    @Composable
    override fun present(): LibraryDetailScreenState {
        val state = rememberLibraryDetailPresenter(dataSource).present()
        val scope = rememberCoroutineScope()
        return LibraryDetailScreenState(
            dataSource = dataSource,
            state = state,
        ) { event ->
            with(repository) {
                with(popupController) {
                    when (event) {
                        LibraryDetailScreenEvent.OnBackKeyPressed -> navigator.pop()
                        is LibraryDetailScreenEvent.OnMediaItemClick ->
                            if (dataSource.browseable()) {
                                navigator.goTo(LibraryDetailScreen(event.mediaItem.asLibraryDataSource()))
                            } else {
                                state.eventSink.invoke(LibraryContentEvent.OnRequestPlay(event.mediaItem as AudioItemModel))
                            }

                        LibraryDetailScreenEvent.OnOptionClick -> {
                            val item = state.mediaItem ?: return@with
                            scope.launch { showLibraryMediaOption(item) }
                        }
                    }
                }
            }
        }
    }
}

data class LibraryDetailScreenState(
    val dataSource: LibraryDataSource,
    val state: LibraryContentState,
    val eventSink: (LibraryDetailScreenEvent) -> Unit,
) : CircuitUiState

sealed interface LibraryDetailScreenEvent {
    data object OnBackKeyPressed : LibraryDetailScreenEvent

    data object OnOptionClick : LibraryDetailScreenEvent

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
