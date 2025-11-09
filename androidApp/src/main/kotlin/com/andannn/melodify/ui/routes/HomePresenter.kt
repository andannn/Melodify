/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import com.andannn.melodify.core.syncer.SyncMediaStoreHandler
import com.andannn.melodify.core.syncer.SyncStatus
import com.andannn.melodify.core.syncer.SyncType
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.SnackBarMessage
import com.andannn.melodify.ui.Screen
import com.andannn.melodify.ui.components.tab.TabUiState
import com.andannn.melodify.ui.components.tab.rememberTabUiPresenter
import com.andannn.melodify.ui.components.tabcontent.TabContentState
import com.andannn.melodify.ui.components.tabcontent.rememberTabContentPresenter
import com.andannn.melodify.ui.core.LaunchNavigationRequestHandlerEffect
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.default_sort_order
import melodify.shared.ui.generated.resources.re_sync_media_library
import melodify.shared.ui.generated.resources.sync_progress_album
import melodify.shared.ui.generated.resources.sync_progress_artist
import melodify.shared.ui.generated.resources.sync_progress_genre
import melodify.shared.ui.generated.resources.sync_progress_media
import melodify.shared.ui.generated.resources.sync_progress_video
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberHomeUiPresenter(
    navigator: Navigator,
    popController: PopupController = LocalPopupController.current,
    syncMediaStoreHandler: SyncMediaStoreHandler = getKoin().get(),
): Presenter<HomeState> =
    retain(
        navigator,
        popController,
        syncMediaStoreHandler,
    ) {
        HomePresenter(
            navigator,
            popController,
            syncMediaStoreHandler,
        )
    }

enum class MenuOption(
    val textRes: StringResource,
) {
    DEFAULT_SORT(
        textRes = Res.string.default_sort_order,
    ),
    RE_SYNC_ALL_MEDIA(
        textRes = Res.string.re_sync_media_library,
    ),
}

@Stable
data class HomeState(
    val tabUiState: TabUiState,
    val tabContentState: TabContentState,
    val eventSink: (HomeUiEvent) -> Unit = {},
)

sealed interface HomeUiEvent {
    data object SearchButtonClick : HomeUiEvent

    data object LibraryButtonClick : HomeUiEvent

    data class OnMenuSelected(
        val selected: MenuOption,
    ) : HomeUiEvent

    data object OnTabManagementClick : HomeUiEvent
}

private const val TAG = "HomeScreen"

private class HomePresenter(
    private val navigator: Navigator,
    private val popController: PopupController,
    private val syncMediaStoreHandler: SyncMediaStoreHandler,
) : ScopedPresenter<HomeState>() {
    @Composable
    override fun present(): HomeState {
        val tabUiState = rememberTabUiPresenter().present()
        val tabContentPresenter = rememberTabContentPresenter(selectedTab = tabUiState.selectedTab)

        LaunchNavigationRequestHandlerEffect(
            navigator = navigator,
            eventSink = tabContentPresenter,
        )

        return HomeState(
            tabUiState = tabUiState,
            tabContentState = tabContentPresenter.present(),
        ) { eventSink ->
            with(popController) {
                with(syncMediaStoreHandler) {
                    when (eventSink) {
                        HomeUiEvent.LibraryButtonClick -> navigator.navigateTo(Screen.Library)
                        HomeUiEvent.SearchButtonClick -> navigator.navigateTo(Screen.Search)
                        is HomeUiEvent.OnMenuSelected -> {
                            when (eventSink.selected) {
                                MenuOption.DEFAULT_SORT -> launch { changeSortRule() }
                                MenuOption.RE_SYNC_ALL_MEDIA -> launch { resyncAllSongs() }
                            }
                        }

                        HomeUiEvent.OnTabManagementClick -> navigator.navigateTo(Screen.TabManage)
                    }
                }
            }
        }
    }
}

context(syncMediaStoreHandler: SyncMediaStoreHandler, popController: PopupController)
private suspend fun resyncAllSongs() =
    coroutineScope {
        var job: Job? = null
        var mediaCount: Int? = null
        syncMediaStoreHandler.reSyncAllMedia().collect {
            Napier.d(tag = TAG) { "sync status update $it" }
            job?.cancel()
            job =
                launch {
                    if (it is SyncStatus.Progress && it.type == SyncType.MEDIA) {
                        mediaCount = it.total
                    }

                    delay(50)

                    when (it) {
                        SyncStatus.Complete ->
                            popController.showSnackBar(
                                SnackBarMessage.SyncCompleted(mediaCount ?: 0),
                            )

                        SyncStatus.Failed ->
                            popController.showSnackBar(
                                SnackBarMessage.SyncFailed,
                            )

                        is SyncStatus.Progress -> {
                            popController.showSnackBar(
                                SnackBarMessage.SyncProgress(it.toSnackBarInfoString()),
                            )
                        }

                        SyncStatus.Start ->
                            popController.showSnackBar(
                                SnackBarMessage.SyncStatusStart,
                            )
                    }
                }
        }
    }

context(popupController: PopupController)
private suspend fun changeSortRule() {
    popupController.showDialog(
        DialogId.DefaultSortRuleSettingDialog,
    )
}

private suspend fun SyncStatus.Progress.toSnackBarInfoString(): String =
    when (type) {
        SyncType.MEDIA -> getString(Res.string.sync_progress_media, progress, total)
        SyncType.ALBUM -> getString(Res.string.sync_progress_album, progress, total)
        SyncType.ARTIST -> getString(Res.string.sync_progress_artist, progress, total)
        SyncType.GENRE -> getString(Res.string.sync_progress_genre, progress, total)
        SyncType.VIDEO -> getString(Res.string.sync_progress_video, progress, total)
    }
