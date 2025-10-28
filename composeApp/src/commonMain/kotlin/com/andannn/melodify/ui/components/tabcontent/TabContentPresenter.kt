/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.core.data.model.contentFlow
import com.andannn.melodify.core.data.model.contentPagingDataFlow
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.popup.addToNextPlay
import com.andannn.melodify.ui.popup.addToPlaylist
import com.andannn.melodify.ui.popup.addToQueue
import com.andannn.melodify.ui.popup.dialog.OptionItem
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val TAG = "TabContentPresenter"

@Composable
fun rememberTabContentPresenter(
    selectedTab: CustomTab?,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
) = remember(
    selectedTab,
    repository,
    popupController,
) {
    TabContentPresenter(
        selectedTab,
        repository,
        popupController,
    )
}

class TabContentPresenter(
    private val selectedTab: CustomTab?,
    private val repository: Repository,
    private val popupController: PopupController,
) : Presenter<TabContentState> {
    private val mediaControllerRepository = repository.mediaControllerRepository
    private val playListRepository = repository.playListRepository
    private val userPreferenceRepository = repository.userPreferenceRepository

    init {
        Napier.d(tag = TAG) { "TabContentPresenter init $selectedTab" }
    }

    @Composable
    override fun present(): TabContentState {
        val scope = rememberCoroutineScope()
        Napier.d(tag = TAG) { "TabContentPresenter scope ${scope.hashCode()}" }

        var groupSort by rememberRetained(selectedTab) {
            mutableStateOf(
                SortRule.Preset.ArtistAlbumASC,
            )
        }

        LaunchedEffect(selectedTab) {
            val currentTab = selectedTab
            if (currentTab == null) return@LaunchedEffect

            userPreferenceRepository.getCurrentSortRule(currentTab).collect {
                groupSort = it
            }
        }

        val pagingDataFlow =
            rememberRetained(selectedTab, groupSort) {
                getContentPagingFlow(selectedTab, groupSort).cachedIn(scope)
            }
        val pagingItems: LazyPagingItems<AudioItemModel> = pagingDataFlow.collectAsLazyPagingItems()

        DisposableEffect(Unit) {
            onDispose {
                Napier.d(tag = TAG) { "TabContentPresenter onDispose $selectedTab" }
            }
        }

        return TabContentState(
            selectedTab = selectedTab,
            groupSort = groupSort,
            pagingItems = pagingItems,
        ) { eventSink ->
            when (eventSink) {
                is TabContentEvent.OnPlayMusic ->
                    scope.launch {
                        val items =
                            with(repository) {
                                selectedTab
                                    ?.contentFlow(sort = groupSort)
                                    ?.first()
                                    ?: error("selectedTab is null")
                            }
                        playMusic(
                            eventSink.mediaItemModel,
                            allAudios = items,
                        )
                    }

                is TabContentEvent.OnShowMusicItemOption ->
                    scope.launch {
                        onShowMusicItemOption(
                            eventSink.mediaItemModel,
                        )
                    }
            }
        }
    }

    private fun getContentPagingFlow(
        selectedTab: CustomTab?,
        groupSort: SortRule,
    ): Flow<PagingData<AudioItemModel>> {
        if (selectedTab == null) {
            return flowOf()
        }
        return with(repository) {
            selectedTab.contentPagingDataFlow(
                sort = groupSort,
            )
        }
    }

    private suspend fun playMusic(
        mediaItem: AudioItemModel,
        allAudios: List<AudioItemModel>,
    ) {
        if (mediaItem.isValid()) {
            mediaControllerRepository.playMediaList(
                allAudios.toList(),
                allAudios.indexOf(mediaItem),
            )
        } else {
            Napier.d(tag = TAG) { "invalid media item click $mediaItem" }
            val result =
                popupController.showDialog(DialogId.ConfirmDeletePlaylist)
            Napier.d(tag = TAG) { "ConfirmDeletePlaylist result: $result" }
            if (result == DialogAction.AlertDialog.Accept) {
                val playListId = (selectedTab as CustomTab.PlayListDetail).playListId
                val mediaId = mediaItem.id.substringAfter(AudioItemModel.INVALID_ID_PREFIX)

                playListRepository.removeMusicFromPlayList(playListId.toLong(), listOf(mediaId))
            }
        }
    }

    private suspend fun onShowMusicItemOption(item: AudioItemModel) {
        val options =
            listOf(
                OptionItem.PLAY_NEXT,
                OptionItem.ADD_TO_QUEUE,
                OptionItem.ADD_TO_PLAYLIST,
            )
        val result =
            popupController.showDialog(
                DialogId.OptionDialog(
                    options = options,
                ),
            )
        if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
            context(repository, popupController) {
                when (result.optionItem) {
                    OptionItem.PLAY_NEXT -> addToNextPlay(listOf(item))
                    OptionItem.ADD_TO_QUEUE -> addToQueue(listOf(item))
                    OptionItem.ADD_TO_PLAYLIST -> addToPlaylist(listOf(item))
                    else -> {}
                }
            }
        }
    }
}

enum class GroupType {
    ARTIST,
    Genre,
    YEAR,
    ALBUM,
    TITLE,
    NONE,
}

data class TabContentState(
    val selectedTab: CustomTab? = null,
    val groupSort: SortRule,
    val pagingItems: LazyPagingItems<AudioItemModel>,
    val eventSink: (TabContentEvent) -> Unit = {},
) : CircuitUiState

sealed interface TabContentEvent {
    data class OnShowMusicItemOption(
        val mediaItemModel: AudioItemModel,
    ) : TabContentEvent

    data class OnPlayMusic(
        val mediaItemModel: AudioItemModel,
    ) : TabContentEvent
}
