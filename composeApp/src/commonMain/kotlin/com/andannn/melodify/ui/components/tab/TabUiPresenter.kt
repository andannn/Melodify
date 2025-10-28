/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.core.data.model.contentFlow
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.popup.addToNextPlay
import com.andannn.melodify.ui.popup.addToPlaylist
import com.andannn.melodify.ui.popup.addToQueue
import com.andannn.melodify.ui.popup.dialog.OptionItem
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

private const val TAG = "TabUiState"

@Composable
fun rememberTabUiPresenter(
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
) = remember(repository, popupController) {
    TabUiPresenter(
        repository,
        popupController,
    )
}

class TabUiPresenter(
    private val repository: Repository,
    private val popupController: PopupController,
) : Presenter<TabUiState> {
    private val userPreferenceRepository = repository.userPreferenceRepository

    @Composable
    override fun present(): TabUiState {
        Napier.d(tag = TAG) { "TabUiPresenter present" }
        val scope = rememberCoroutineScope()
        var selectedIndex by rememberSaveable { mutableStateOf(0) }
        val currentTabList by userPreferenceRepository.currentCustomTabsFlow.collectAsRetainedState(
            emptyList(),
        )

        suspend fun currentItems(): List<AudioItemModel> {
            val currentTab =
                currentTabList.getOrNull(selectedIndex) ?: return emptyList()
            val groupSort =
                userPreferenceRepository.getSortRule(currentTab).first()
            return with(repository) {
                currentTab.contentFlow(sort = groupSort).first()
            }
        }

        LaunchedEffect(Unit) {
            userPreferenceRepository.currentCustomTabsFlow
                .scan<List<CustomTab>, Pair<List<CustomTab>?, List<CustomTab>?>>(null to null) { pre, next ->
                    pre.second to next
                }.collect { (pre, next) ->
                    Napier.d(tag = TAG) { "tab changed pre: $pre, next: $next" }
                    if (pre == null || next == null) {
                        return@collect
                    }

                    val currentIndex = selectedIndex

                    val newIndex: Int =
                        if (next.size < pre.size) {
                            // new tab list is smaller than the previous one.
                            // 1. select the previous tab
                            // 2. if the current tab is removed, select the previous tab
                            next
                                .indexOf(pre.getOrNull(currentIndex))
                                .takeIf { it != -1 } ?: (currentIndex - 1).coerceAtLeast(0)
                        } else if (next.size > pre.size) {
                            // always select the new created tab
                            next.indexOf(next.firstOrNull { it !in pre })
                        } else {
                            next.indexOf(pre.getOrNull(currentIndex))
                        }
                    if (newIndex != -1) {
                        selectedIndex = newIndex
                    }
                }
        }
        return TabUiState(
            selectedIndex.coerceAtMost(currentTabList.size - 1),
            currentTabList,
        ) { eventSink ->
            when (eventSink) {
                is TabUiEvent.OnClickTab -> selectedIndex = eventSink.index
                is TabUiEvent.OnShowTabOption ->
                    scope.launch {
                        val tab = eventSink.tab
                        val result =
                            popupController.showDialog(
                                DialogId.OptionDialog(
                                    options =
                                        buildList {
                                            add(OptionItem.PLAY_NEXT)
                                            add(OptionItem.ADD_TO_QUEUE)
                                            add(OptionItem.ADD_TO_PLAYLIST)
                                            add(OptionItem.DISPLAY_SETTING)

                                            if (tab !is CustomTab.AllMusic) {
                                                add(OptionItem.DELETE_TAB)
                                            }
                                        },
                                ),
                            )

                        if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
                            context(repository, popupController) {
                                when (result.optionItem) {
                                    OptionItem.DELETE_TAB ->
                                        repository.userPreferenceRepository.deleteCustomTab(tab)

                                    OptionItem.PLAY_NEXT -> currentItems().also { addToNextPlay(it) }
                                    OptionItem.ADD_TO_QUEUE -> currentItems().also { addToQueue(it) }
                                    OptionItem.ADD_TO_PLAYLIST ->
                                        currentItems().also { addToPlaylist(it) }

                                    OptionItem.DISPLAY_SETTING -> {
                                        popupController.showDialog(DialogId.ChangeSortRuleDialog(tab))
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }
            }
        }
    }
}

data class TabUiState(
    val selectedIndex: Int = 0,
    val customTabList: List<CustomTab> = emptyList(),
    val eventSink: (TabUiEvent) -> Unit = {},
) : CircuitUiState {
    val selectedTab: CustomTab?
        get() = customTabList.getOrNull(selectedIndex)
}

sealed interface TabUiEvent {
    data class OnClickTab(
        val index: Int,
    ) : TabUiEvent

    data class OnShowTabOption(
        val tab: CustomTab,
    ) : TabUiEvent
}
