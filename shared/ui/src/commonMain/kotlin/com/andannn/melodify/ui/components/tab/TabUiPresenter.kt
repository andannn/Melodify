/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.sortOptions
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.RetainedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import com.andannn.melodify.usecase.addToNextPlay
import com.andannn.melodify.usecase.addToPlaylist
import com.andannn.melodify.usecase.addToQueue
import com.andannn.melodify.usecase.contentFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

private const val TAG = "TabUiState"

@Composable
fun retainTabUiPresenter(
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
) = retainPresenter(repository, popupController) {
    TabUiPresenter(
        repository,
        popupController,
    )
}

class TabUiPresenter(
    private val repository: Repository,
    private val popupController: PopupController,
) : RetainedPresenter<TabUiState>() {
    private val userPreferenceRepository = repository.userPreferenceRepository

    var currentTabList by mutableStateOf<List<CustomTab>>(
        emptyList(),
    )
    var selectedIndex by mutableIntStateOf(0)

    init {
        retainedScope.launch {
            userPreferenceRepository.currentCustomTabsFlow.collect {
                currentTabList = it
            }
        }

        retainedScope.launch {
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
    }

    private suspend fun currentItems(): List<MediaItemModel> {
        val currentTab =
            currentTabList.getOrNull(selectedIndex) ?: return emptyList()
        val groupSort =
            userPreferenceRepository.getCurrentSortRule(currentTab).first()
        return with(repository) {
            currentTab.contentFlow(sorts = groupSort.sortOptions()).first()
        }
    }

    @Composable
    override fun present(): TabUiState {
        Napier.d(tag = TAG) { "TabUiPresenter present" }
        return TabUiState(
            selectedIndex.coerceAtMost(currentTabList.size - 1),
            currentTabList,
        ) { eventSink ->
            when (eventSink) {
                is TabUiEvent.OnClickTab -> selectedIndex = eventSink.index
                is TabUiEvent.OnShowTabOption ->
                    retainedScope.launch {
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
                                            add(OptionItem.DELETE_TAB)
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
                                        currentItems().also { list ->
// TODO: Video playlist impl
                                            addToPlaylist(list as List<AudioItemModel>)
                                        }

                                    OptionItem.DISPLAY_SETTING ->
                                        popupController.showDialog(DialogId.ChangeSortRuleDialog(tab))

                                    else -> {}
                                }
                            }
                        }
                    }
            }
        }
    }
}

@Stable
data class TabUiState(
    val selectedIndex: Int = 0,
    val customTabList: List<CustomTab> = emptyList(),
    val eventSink: (TabUiEvent) -> Unit = {},
) {
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
