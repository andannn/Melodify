/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.model.asLibraryDataSource
import com.andannn.melodify.model.browseable
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.components.librarydetail.LibraryContentEvent
import com.andannn.melodify.ui.components.librarydetail.LibraryContentState
import com.andannn.melodify.ui.components.librarydetail.rememberLibraryDetailPresenter
import com.andannn.melodify.ui.components.librarydetail.showLibraryMediaOption
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberLibraryDetailScreenPresenter(
    dataSource: LibraryDataSource,
    navigator: Navigator,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
    mediaFileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
): Presenter<LibraryDetailScreenState> =
    retain(
        dataSource,
        navigator,
        repository,
        popupController,
        mediaFileDeleteHelper,
    ) {
        LibraryDetailScreenPresenter(dataSource, navigator, repository, popupController, mediaFileDeleteHelper)
    }

@Stable
data class LibraryDetailScreenState(
    val dataSource: LibraryDataSource,
    val state: LibraryContentState,
    val eventSink: (LibraryDetailScreenEvent) -> Unit,
)

sealed interface LibraryDetailScreenEvent {
    data object OnBackKeyPressed : LibraryDetailScreenEvent

    data object OnOptionClick : LibraryDetailScreenEvent

    data class OnMediaItemClick(
        val mediaItem: MediaItemModel,
    ) : LibraryDetailScreenEvent
}

private class LibraryDetailScreenPresenter(
    private val dataSource: LibraryDataSource,
    private val navigator: Navigator,
    private val repository: Repository,
    private val popupController: PopupController,
    private val fileDeleteHelper: MediaFileDeleteHelper,
) : ScopedPresenter<LibraryDetailScreenState>() {
    @Composable
    override fun present(): LibraryDetailScreenState {
        val state = rememberLibraryDetailPresenter(dataSource).present()
        return LibraryDetailScreenState(
            dataSource = dataSource,
            state = state,
        ) { event ->
            context(repository, popupController, fileDeleteHelper) {
                when (event) {
                    LibraryDetailScreenEvent.OnBackKeyPressed -> navigator.popBackStack()
                    is LibraryDetailScreenEvent.OnMediaItemClick ->
                        if (dataSource.browseable()) {
                            navigator.navigateTo(Screen.LibraryDetail(event.mediaItem.asLibraryDataSource()))
                        } else {
                            state.eventSink.invoke(LibraryContentEvent.OnRequestPlay(event.mediaItem as AudioItemModel))
                        }

                    LibraryDetailScreenEvent.OnOptionClick -> {
                        state.mediaItem?.let { item ->
                            launch { showLibraryMediaOption(item) }
                        }
                    }
                }
            }
        }
    }
}
