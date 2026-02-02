/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.andannn.melodify.ui.Screen.*
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
internal data class HomeState
    @OptIn(ExperimentalMaterial3Api::class)
    constructor(
        val textFieldState: TextFieldState,
        val searchBarState: SearchBarState,
        val tabUiState: TabUiState,
        val tabContentState: TabContentState,
        val eventSink: (HomeUiEvent) -> Unit = {},
    )

internal sealed interface HomeUiEvent {
    data object SearchButtonClick : HomeUiEvent

    data object LibraryButtonClick : HomeUiEvent

    data object OnBackFullScreen : HomeUiEvent

    data class OnMenuSelected(
        val selected: MenuOption,
    ) : HomeUiEvent

    data object OnTabManagementClick : HomeUiEvent

    data class OnSearchResultItemClick(
        val result: MediaItemModel,
    ) : HomeUiEvent

    data class OnConfirmSearch(
        val text: String,
    ) : HomeUiEvent
}

private class HomePresenter(
    private val navigator: Navigator,
    private val popupHostState: PopupHostState,
    private val repository: Repository,
) : RetainedPresenter<HomeState>() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun present(): HomeState {
        val tabUiState = retainTabUiPresenter().present()
        val tabContentPresenter =
            retainTabContentPresenter(selectedTab = tabUiState.selectedTab)
        val textFieldState = rememberTextFieldState()
        val searchBarState = rememberSearchBarState()
        val animationScope = rememberCoroutineScope()

        fun resetSearchState() {
            textFieldState.clearText()
        }

        fun collapsedSearchScreen() {
            animationScope.launch {
                searchBarState.animateToCollapsed()
            }
        }

        return HomeState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            tabUiState = tabUiState,
            tabContentState = tabContentPresenter.present(),
        ) { eventSink ->
            context(popupHostState, repository) {
                when (eventSink) {
                    HomeUiEvent.LibraryButtonClick -> {
                        navigator.navigateTo(Library)
                    }

                    HomeUiEvent.SearchButtonClick -> {
                        navigator.navigateTo(Search)
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
                        navigator.navigateTo(TabManage)
                    }

                    is HomeUiEvent.OnSearchResultItemClick -> {
                        resetSearchState()
                        collapsedSearchScreen()

                        val item = eventSink.result
                        if (item.browsable) {
                            navigator.navigateTo(
                                LibraryDetail(item.asLibraryDataSource()),
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

                    is HomeUiEvent.OnConfirmSearch -> {
                        animationScope.launch {
                            searchBarState.animateToCollapsed()
                        }
                    }

                    HomeUiEvent.OnBackFullScreen -> {
                        resetSearchState()
                        collapsedSearchScreen()
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
