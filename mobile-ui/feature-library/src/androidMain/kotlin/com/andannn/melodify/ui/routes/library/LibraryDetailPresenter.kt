/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.components.librarydetail.LibraryContentState
import com.andannn.melodify.ui.components.librarydetail.retainLibraryDetailPresenter
import com.andannn.melodify.ui.core.LocalNavigationRequestEventSink
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.RetainedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import com.andannn.melodify.ui.core.showDialogAndWaitAction
import com.andannn.melodify.ui.popup.DialogAction
import com.andannn.melodify.ui.popup.OptionDialog
import com.andannn.melodify.usecase.pinAllMusicToHomeTab
import com.andannn.melodify.usecase.pinAllVideoToHomeTab
import com.andannn.melodify.usecase.showLibraryMediaOption
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun retainLibraryDetailScreenPresenter(
    dataSource: LibraryDataSource,
    navigator: Navigator,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
    mediaFileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
): Presenter<LibraryDetailScreenState> =
    retainPresenter(
        dataSource,
        navigator,
        repository,
        popupController,
        mediaFileDeleteHelper,
        navigationRequestEventSink,
    ) {
        LibraryDetailScreenPresenter(
            dataSource,
            navigator,
            repository,
            popupController,
            mediaFileDeleteHelper,
            navigationRequestEventSink,
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
    private val navigationRequestEventSink: NavigationRequestEventSink,
) : RetainedPresenter<LibraryDetailScreenState>() {
    @Composable
    override fun present(): LibraryDetailScreenState {
        val presenter = retainLibraryDetailPresenter(dataSource)
        val state = presenter.present()

        return LibraryDetailScreenState(
            dataSource = dataSource,
            state = state,
        ) { event ->
            context(repository, popupController, fileDeleteHelper, navigationRequestEventSink) {
                when (event) {
                    LibraryDetailScreenEvent.OnBackKeyPressed -> {
                        navigator.popBackStack()
                    }

                    LibraryDetailScreenEvent.OnOptionClick -> {
                        when (dataSource) {
                            LibraryDataSource.AllSong,
                            LibraryDataSource.AllVideo,
                            -> {
                                retainedScope.launch {
                                    val result =
                                        popupController.showDialogAndWaitAction(
                                            OptionDialog(
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
                            }

                            else -> {
                                state.mediaItem?.let { item ->
                                    retainedScope.launch { showLibraryMediaOption(item) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
