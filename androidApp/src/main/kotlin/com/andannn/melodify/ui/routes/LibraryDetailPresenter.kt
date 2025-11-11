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
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.model.asLibraryDataSource
import com.andannn.melodify.model.browseable
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.components.librarydetail.LibraryContentEvent
import com.andannn.melodify.ui.components.librarydetail.LibraryContentState
import com.andannn.melodify.ui.components.librarydetail.rememberLibraryDetailPresenter
import com.andannn.melodify.ui.core.LaunchNavigationRequestHandlerEffect
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.usecase.pinAllMusicToHomeTab
import com.andannn.melodify.usecase.pinAllVideoToHomeTab
import com.andannn.melodify.usecase.pinToHomeTab
import com.andannn.melodify.usecase.showLibraryMediaOption
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
        LibraryDetailScreenPresenter(
            dataSource,
            navigator,
            repository,
            popupController,
            mediaFileDeleteHelper,
        )
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
        val presenter = rememberLibraryDetailPresenter(dataSource)
        val state = presenter.present()

        LaunchNavigationRequestHandlerEffect(
            navigator = navigator,
            eventSink = presenter as NavigationRequestEventSink,
        )

        return LibraryDetailScreenState(
            dataSource = dataSource,
            state = state,
        ) { event ->
            context(repository, popupController, fileDeleteHelper) {
                when (event) {
                    LibraryDetailScreenEvent.OnBackKeyPressed -> navigator.popBackStack()

                    LibraryDetailScreenEvent.OnOptionClick -> {
                        when (dataSource) {
                            LibraryDataSource.AllSong,
                            LibraryDataSource.AllVideo,
                            ->
                                launch {
                                    val result =
                                        popupController.showDialog(
                                            DialogId.OptionDialog(
                                                listOf(
                                                    OptionItem.ADD_TO_HOME_TAB,
                                                ),
                                            ),
                                        )

                                    if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
                                        when (result.optionItem) {
                                            OptionItem.ADD_TO_HOME_TAB -> {
                                                if (dataSource == LibraryDataSource.AllSong) {
                                                    pinAllMusicToHomeTab()
                                                } else {
                                                    pinAllVideoToHomeTab()
                                                }
                                            }

                                            else -> {}
                                        }
                                    }
                                }

                            else -> {
                                state.mediaItem?.let { item ->
                                    launch { showLibraryMediaOption(item) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
