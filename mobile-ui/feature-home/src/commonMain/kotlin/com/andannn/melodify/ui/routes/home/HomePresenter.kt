/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.andannn.melodify.core.syncer.MediaLibrarySyncRepository
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.browsable
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.asLibraryDataSource
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.components.tab.TabUiState
import com.andannn.melodify.shared.compose.components.tab.content.TabContentState
import com.andannn.melodify.shared.compose.components.tab.content.retainTabContentPresenter
import com.andannn.melodify.shared.compose.components.tab.retainTabUiPresenter
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.entry.sort.rule.DefaultSortRuleSettingPopup
import com.andannn.melodify.shared.compose.popup.entry.sync.SyncStatusPopup
import com.andannn.melodify.shared.compose.usecase.playMediaItems
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.Screen
import io.github.andannn.popup.PopupHostState
import kotlinx.coroutines.launch
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.default_sort_order
import melodify.shared.compose.resource.generated.resources.re_sync_media_library
import org.jetbrains.compose.resources.StringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun retainHomeUiPresenter(
    navigator: Navigator,
    popController: PopupHostState = LocalPopupHostState.current,
    repository: Repository = getKoin().get(),
): Presenter<HomeState> =
    retainPresenter(
        navigator,
        popController,
        repository,
    ) {
        HomePresenter(
            navigator,
            popController,
            repository,
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

    data class OnSearchResultItemClick(
        val result: MediaItemModel,
    ) : HomeUiEvent
}

private class HomePresenter(
    private val navigator: Navigator,
    private val popupHostState: PopupHostState,
    private val repository: Repository,
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
            context(popupHostState, repository) {
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

                    is HomeUiEvent.OnSearchResultItemClick -> {
                        val item = eventSink.result
                        if (item.browsable) {
                            navigator.navigateTo(
                                Screen.LibraryDetail(item.asLibraryDataSource()),
                            )
                        } else {
                            retainedScope.launch {
                                playMediaItems(
                                    item,
                                    listOf(item),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

context(popupHostState: PopupHostState)
private suspend fun changeSortRule() {
    popupHostState.showDialog(
        DefaultSortRuleSettingPopup,
    )
}

context(popupHostState: PopupHostState)
private suspend fun showSyncDialog() {
    popupHostState.showDialog(
        SyncStatusPopup,
    )
}
