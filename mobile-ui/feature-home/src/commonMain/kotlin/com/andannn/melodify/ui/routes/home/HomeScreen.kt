/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
    searchBarPresenter: Presenter<SearchBarLayoutState> = retainSearchBarPresenter(),
    popupHostState: PopupHostState = LocalPopupHostState.current,
) {
    val tabUiState = retainTabUiPresenter().present()
    val tabContentState =
        retainTabContentPresenter(selectedTab = tabUiState.selectedTab).present()
    val searchBarState = searchBarPresenter.present()

    val scope = rememberCoroutineScope()
    val isPopupShowing = popupHostState.currentPopup != null
    NavigationEventHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = searchBarState.currentContent is ContentState.Search,
    ) {
        searchBarState.eventSink.invoke(SearchBarUiEvent.OnExitSearch)
    }

    SearchScaffoldLayout(
        modifier = modifier,
        enabled = !isPopupShowing,
        searchBarLayoutState = searchBarState,
        onLibraryButtonClick = {
            navigator.navigateTo(Screen.Library)
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
        AnimatedContent(
            searchBarState.currentContent,
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            },
        ) { state ->
            when (state) {
                is ContentState.Library -> {
                    Column {
                        TabUi(
                            state = tabUiState,
                            onTabManagementClick = {
                                navigator.navigateTo(Screen.TabManage)
                            },
                        )

                        TabContent(tabContentState, modifier = Modifier)
                    }
                }

                is ContentState.Search -> {
                    SearchResultPage(
                        query = state.query,
                        onResultItemClick = {
                            searchBarState.eventSink.invoke(
                                SearchBarUiEvent.OnSearchResultItemClick(
                                    it,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }

    ResumePointIndicatorContainer(
        modifier =
            Modifier
                .zIndex(1f),
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
