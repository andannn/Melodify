/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.components.play.control.ResumePointIndicatorContainer
import com.andannn.melodify.shared.compose.components.search.result.SearchResultPage
import com.andannn.melodify.shared.compose.components.tab.TabUi
import com.andannn.melodify.shared.compose.components.tab.content.TabContent
import com.andannn.melodify.shared.compose.components.tab.content.retainTabContentPresenter
import com.andannn.melodify.shared.compose.components.tab.retainTabUiPresenter
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.entry.sort.rule.DefaultSortRuleSettingPopup
import com.andannn.melodify.shared.compose.popup.entry.sync.SyncStatusPopup
import com.andannn.melodify.ui.Navigator
import com.andannn.melodify.ui.Screen
import io.github.andannn.popup.PopupHostState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun HomeUiScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    presenter: Presenter<HomeLayoutState> = retainHomeLayoutPresenter(),
    popupHostState: PopupHostState = LocalPopupHostState.current,
) {
    val tabUiState = retainTabUiPresenter().present()
    val tabContentState = retainTabContentPresenter(selectedTab = tabUiState.selectedTab).present()
    val layoutState = presenter.present()
    val selectedMediaSet = layoutState.selectedMediaSet
    val selectedGroup = layoutState.selectedGroup
    val scope = rememberCoroutineScope()

    NavigationEventHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = layoutState.homeState is HomeState.Search,
    ) {
        layoutState.eventSink.invoke(HomeLayoutEvent.OnExitSearch)
    }
    NavigationEventHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = layoutState.homeState is HomeState.MultiSelecting,
    ) {
        layoutState.eventSink.invoke(HomeLayoutEvent.OnExitSelecting)
    }

    HomeScaffoldLayout(
        modifier = modifier,
        homeLayoutState = layoutState,
        onExitSelecting = {
            layoutState.eventSink.invoke(HomeLayoutEvent.OnExitSelecting)
        },
        onMultiSelectionOptionClick = {
            layoutState.eventSink.invoke(HomeLayoutEvent.OnMultiSelectionOptionClick)
        },
        onMenuSelected = { selected ->
            when (selected) {
                MenuOption.DEFAULT_SORT -> {
                    scope.launch { popupHostState.changeSortRule() }
                }

                MenuOption.RE_SYNC_ALL_MEDIA -> {
                    scope.launch { popupHostState.showSyncDialog() }
                }
            }
        },
    ) {
        when (val state = layoutState.homeState) {
            HomeState.Library,
            HomeState.MultiSelecting,
            -> {
                Column {
                    TabUi(
                        state = tabUiState,
                        onTabManagementClick = {
                            scope.launch { popupHostState.showDialog(TabManagementDialogID) }
                        },
                    )

                    TabContent(
                        state = tabContentState,
                        modifier = Modifier,
                        isInSelectingMode = state is HomeState.MultiSelecting,
                        selectedMediaItemSet = selectedMediaSet,
                        selectedGroupSet = selectedGroup,
                        onClickHeaderWhenSelecting = { selectedTab, groupKey ->
                            layoutState.eventSink.invoke(
                                HomeLayoutEvent.OnClickHeaderWhenSelecting(
                                    selectedTab,
                                    groupKey,
                                ),
                            )
                        },
                        onClickMediaItemWhenSelecting = {
                            layoutState.eventSink.invoke(
                                HomeLayoutEvent.OnClickMediaItemWhenSelecting(
                                    it,
                                ),
                            )
                        },
                    )
                }
            }

            is HomeState.Search -> {
                SearchResultPage(
                    query = state.query,
                    onResultItemClick = {
                        layoutState.eventSink.invoke(
                            HomeLayoutEvent.OnSearchResultItemClick(
                                it,
                            ),
                        )
                    },
                )
            }
        }
    }

    ResumePointIndicatorContainer(
        modifier = Modifier.zIndex(1f),
    )
}

private suspend fun PopupHostState.changeSortRule() {
    showDialog(
        DefaultSortRuleSettingPopup,
    )
}

private suspend fun PopupHostState.showSyncDialog() {
    showDialog(
        SyncStatusPopup,
    )
}
