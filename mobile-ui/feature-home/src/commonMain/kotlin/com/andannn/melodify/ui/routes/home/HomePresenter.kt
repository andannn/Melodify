/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.andannn.melodify.core.syncer.MediaLibrarySyncRepository
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.components.tab.TabUiState
import com.andannn.melodify.shared.compose.components.tab.content.TabContentState
import com.andannn.melodify.shared.compose.components.tab.content.retainTabContentPresenter
import com.andannn.melodify.shared.compose.components.tab.retainTabUiPresenter
import com.andannn.melodify.shared.compose.popup.DialogHostState
import com.andannn.melodify.shared.compose.popup.LocalDialogHostState
import com.andannn.melodify.shared.compose.popup.entry.sort.rule.DefaultSortRuleSettingDialog
import com.andannn.melodify.shared.compose.popup.entry.sync.SyncStatusDialog
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.Screen
import kotlinx.coroutines.launch
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.default_sort_order
import melodify.shared.compose.resource.generated.resources.re_sync_media_library
import org.jetbrains.compose.resources.StringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun retainHomeUiPresenter(
    navigator: Navigator,
    popController: DialogHostState = LocalDialogHostState.current,
    mediaLibrarySyncRepository: MediaLibrarySyncRepository = getKoin().get(),
): Presenter<HomeState> =
    retainPresenter(
        navigator,
        popController,
        mediaLibrarySyncRepository,
    ) {
        HomePresenter(
            navigator,
            popController,
            mediaLibrarySyncRepository,
        )
    }

internal enum class MenuOption(
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
internal data class HomeState(
    val tabUiState: TabUiState,
    val tabContentState: TabContentState,
    val eventSink: (HomeUiEvent) -> Unit = {},
)

internal sealed interface HomeUiEvent {
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
    private val popController: DialogHostState,
    private val mediaLibrarySyncRepository: MediaLibrarySyncRepository,
) : RetainedPresenter<HomeState>() {
    @Composable
    override fun present(): HomeState {
        val tabUiState = retainTabUiPresenter().present()
        val tabContentPresenter =
            retainTabContentPresenter(selectedTab = tabUiState.selectedTab)

        return HomeState(
            tabUiState = tabUiState,
            tabContentState = tabContentPresenter.present(),
        ) { eventSink ->
            with(popController) {
                when (eventSink) {
                    HomeUiEvent.LibraryButtonClick -> {
                        navigator.navigateTo(Screen.Library)
                    }

                    HomeUiEvent.SearchButtonClick -> {
                        navigator.navigateTo(Screen.Search)
                    }

                    is HomeUiEvent.OnMenuSelected -> {
                        when (eventSink.selected) {
                            MenuOption.DEFAULT_SORT -> {
                                retainedScope.launch { changeSortRule() }
                            }

                            MenuOption.RE_SYNC_ALL_MEDIA -> {
                                retainedScope.launch { showSyncDialog() }
                            }
                        }
                    }

                    HomeUiEvent.OnTabManagementClick -> {
                        navigator.navigateTo(Screen.TabManage)
                    }
                }
            }
        }
    }
}

context(dialogHostState: DialogHostState)
private suspend fun changeSortRule() {
    dialogHostState.showDialog(
        DefaultSortRuleSettingDialog,
    )
}

context(dialogHostState: DialogHostState)
private suspend fun showSyncDialog() {
    dialogHostState.showDialog(
        SyncStatusDialog,
    )
}
