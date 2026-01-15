/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.sortOptions
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.popup.ChangeSortRuleDialog
import com.andannn.melodify.shared.compose.popup.DialogAction
import com.andannn.melodify.shared.compose.popup.LocalPopupController
import com.andannn.melodify.shared.compose.popup.OptionDialog
import com.andannn.melodify.shared.compose.popup.OptionItem
import com.andannn.melodify.shared.compose.popup.PopupController
import com.andannn.melodify.shared.compose.popup.showDialogAndWaitAction
import com.andannn.melodify.shared.compose.popup.snackbar.LocalSnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController
import com.andannn.melodify.shared.compose.usecase.addToNextPlay
import com.andannn.melodify.shared.compose.usecase.addToPlaylist
import com.andannn.melodify.shared.compose.usecase.addToQueue
import com.andannn.melodify.shared.compose.usecase.contentFlow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "TabUiState"

@Composable
fun retainTabUiPresenter(
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
    snackBarController: SnackBarController = LocalSnackBarController.current,
) = retainPresenter(repository, popupController, snackBarController) {
    TabUiPresenter(
        repository,
        popupController,
        snackBarController,
    )
}

class TabUiPresenter(
    private val repository: Repository,
    private val popupController: PopupController,
    private val snackBarController: SnackBarController,
) : RetainedPresenter<TabUiState>() {
    val currentTabListFlow =
        repository.currentCustomTabsFlow
            .stateIn(
                retainedScope,
                initialValue = emptyList(),
                started = SharingStarted.Eagerly,
            )

    val selectedIndexFlow = MutableStateFlow(0)

    init {
        retainedScope.launch {
            repository.currentCustomTabsFlow
                .scan<List<CustomTab>, Pair<List<CustomTab>?, List<CustomTab>?>>(null to null) { pre, next ->
                    pre.second to next
                }.collect { (pre, next) ->
                    Napier.d(tag = TAG) { "tab changed pre: $pre, next: $next" }
                    if (pre == null || next == null) {
                        return@collect
                    }

                    val currentIndex = selectedIndexFlow.value

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
                        selectedIndexFlow.value = newIndex
                    }
                }
        }
    }

    private suspend fun currentItems(): List<MediaItemModel> {
        val currentTab =
            currentTabListFlow.value.getOrNull(selectedIndexFlow.value) ?: return emptyList()
        val groupSort =
            repository.getCurrentSortRule(currentTab).first()
        return with(repository) {
            currentTab.contentFlow(sorts = groupSort.sortOptions()).first()
        }
    }

    @Composable
    override fun present(): TabUiState {
        Napier.d(tag = TAG) { "TabUiPresenter present" }
        val currentTabList by currentTabListFlow.collectAsStateWithLifecycle()
        val selectedIndex by selectedIndexFlow.collectAsStateWithLifecycle()
        return TabUiState(
            selectedIndex.coerceAtMost(currentTabList.size - 1),
            currentTabList,
        ) { eventSink ->
            when (eventSink) {
                is TabUiEvent.OnClickTab -> {
                    selectedIndexFlow.value = eventSink.index
                }

                is TabUiEvent.OnShowTabOption -> {
                    retainedScope.launch {
                        val tab = eventSink.tab
                        val result =
                            popupController.showDialogAndWaitAction(
                                OptionDialog(
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
                            context(repository, popupController, snackBarController) {
                                when (result.optionItem) {
                                    OptionItem.DELETE_TAB -> {
                                        repository.deleteCustomTab(tab)
                                    }

                                    OptionItem.PLAY_NEXT -> {
                                        currentItems().also { addToNextPlay(it) }
                                    }

                                    OptionItem.ADD_TO_QUEUE -> {
                                        currentItems().also { addToQueue(it) }
                                    }

                                    OptionItem.ADD_TO_PLAYLIST -> {
                                        currentItems().also { list -> addToPlaylist(list) }
                                    }

                                    OptionItem.DISPLAY_SETTING -> {
                                        popupController.showDialogAndWaitAction(
                                            ChangeSortRuleDialog(tab),
                                        )
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
