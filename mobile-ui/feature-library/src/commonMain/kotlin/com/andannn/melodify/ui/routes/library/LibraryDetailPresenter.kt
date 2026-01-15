/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.components.library.detail.LibraryContentState
import com.andannn.melodify.shared.compose.components.library.detail.retainLibraryDetailPresenter
import com.andannn.melodify.shared.compose.popup.DialogAction
import com.andannn.melodify.shared.compose.popup.LocalPopupController
import com.andannn.melodify.shared.compose.popup.OptionDialog
import com.andannn.melodify.shared.compose.popup.OptionItem
import com.andannn.melodify.shared.compose.popup.PopupController
import com.andannn.melodify.shared.compose.popup.showDialogAndWaitAction
import com.andannn.melodify.shared.compose.popup.snackbar.LocalSnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController
import com.andannn.melodify.shared.compose.usecase.pinAllMusicToHomeTab
import com.andannn.melodify.shared.compose.usecase.pinAllVideoToHomeTab
import com.andannn.melodify.shared.compose.usecase.showLibraryMediaOption
import com.andannn.melodify.ui.Navigator
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun retainLibraryDetailScreenPresenter(
    dataSource: LibraryDataSource,
    navigator: Navigator,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
    snackBarController: SnackBarController = LocalSnackBarController.current,
    mediaFileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
): Presenter<LibraryDetailScreenState> =
    retainPresenter(
        dataSource,
        navigator,
        repository,
        popupController,
        snackBarController,
        mediaFileDeleteHelper,
        navigationRequestEventSink,
    ) {
        LibraryDetailScreenPresenter(
            dataSource,
            navigator,
            repository,
            popupController,
            snackBarController,
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
    private val snackBarController: SnackBarController,
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
            context(
                repository,
                popupController,
                snackBarController,
                fileDeleteHelper,
                navigationRequestEventSink,
            ) {
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
