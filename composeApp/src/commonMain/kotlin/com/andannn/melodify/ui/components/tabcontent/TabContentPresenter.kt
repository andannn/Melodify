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
import com.andannn.melodify.LocalMediaFileDeleteHelper
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.sortOptions
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.usecase.addToNextPlay
import com.andannn.melodify.usecase.addToPlaylist
import com.andannn.melodify.usecase.addToQueue
import com.andannn.melodify.usecase.contentFlow
import com.andannn.melodify.usecase.contentPagingDataFlow
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
    onRequestGoToAlbum: (AudioItemModel) -> Unit = {},
    onRequestGoToArtist: (AudioItemModel) -> Unit = {},
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
    mediaFileDeleteHelper: MediaFileDeleteHelper = LocalMediaFileDeleteHelper.current,
) = remember(
    selectedTab,
    repository,
    popupController,
    onRequestGoToAlbum,
    onRequestGoToArtist,
    mediaFileDeleteHelper,
) {
    TabContentPresenter(
        selectedTab = selectedTab,
        onRequestGoToAlbum = onRequestGoToAlbum,
        onRequestGoToArtist = onRequestGoToArtist,
        repository = repository,
        popupController = popupController,
        mediaFileDeleteHelper = mediaFileDeleteHelper,
    )
}

class TabContentPresenter(
    private val selectedTab: CustomTab?,
    private val onRequestGoToAlbum: (AudioItemModel) -> Unit = {},
    private val onRequestGoToArtist: (AudioItemModel) -> Unit = {},
    private val repository: Repository,
    private val popupController: PopupController,
    private val mediaFileDeleteHelper: MediaFileDeleteHelper,
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

        var displaySetting by rememberRetained(selectedTab) {
            mutableStateOf(
                DisplaySetting.Preset.ArtistAlbumASC,
            )
        }

        LaunchedEffect(selectedTab) {
            val currentTab = selectedTab
            if (currentTab == null) return@LaunchedEffect

            userPreferenceRepository.getCurrentSortRule(currentTab).collect {
                displaySetting = it
            }
        }

        val pagingDataFlow =
            rememberRetained(selectedTab, displaySetting) {
                getContentPagingFlow(selectedTab, displaySetting).cachedIn(scope)
            }
        val pagingItems: LazyPagingItems<AudioItemModel> = pagingDataFlow.collectAsLazyPagingItems()

        return TabContentState(
            selectedTab = selectedTab,
            groupSort = displaySetting,
            pagingItems = pagingItems,
        ) { eventSink ->
            context(repository, popupController, mediaFileDeleteHelper) {
                when (eventSink) {
                    is TabContentEvent.OnPlayMusic ->
                        scope.launch {
                            val items =
                                with(repository) {
                                    selectedTab
                                        ?.contentFlow(sorts = displaySetting.sortOptions())
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
                            onShowMusicItemOption(eventSink.mediaItemModel)
                        }

                    is TabContentEvent.OnGroupOptionClick ->
                        scope.launch {
                            handleGroupOption(
                                eventSink.optionItem,
                                eventSink.groupKeys,
                                displaySetting,
                                selectedTab,
                            )
                        }

                    is TabContentEvent.OnGroupItemClick ->
                        scope.launch {
                            handleGroupItemClick(
                                eventSink.groupKeys,
                                displaySetting,
                                selectedTab,
                            )
                        }
                }
            }
        }
    }

    private fun getContentPagingFlow(
        selectedTab: CustomTab?,
        groupSort: DisplaySetting,
    ): Flow<PagingData<AudioItemModel>> {
        if (selectedTab == null) {
            return flowOf()
        }
        return with(repository) {
            selectedTab.contentPagingDataFlow(
                sorts = groupSort.sortOptions(),
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

    context(repository: Repository, popupController: PopupController)
    private suspend fun handleGroupItemClick(
        groupKeys: List<GroupKey?>,
        displaySetting: DisplaySetting,
        selectedTab: CustomTab?,
    ) {
        val items =
            selectedTab
                ?.contentFlow(
                    sorts = displaySetting.sortOptions(),
                    whereGroups = groupKeys.filterNotNull(),
                )?.first() ?: emptyList()
        if (items.isNotEmpty()) {
            playMusic(
                items.first(),
                items,
            )
        }
    }

    context(_: Repository, _: PopupController, fileDeleteHelper: MediaFileDeleteHelper)
    private suspend fun handleGroupOption(
        optionItem: OptionItem,
        groupKeys: List<GroupKey?>,
        displaySetting: DisplaySetting,
        selectedTab: CustomTab?,
    ) {
        val items =
            selectedTab
                ?.contentFlow(
                    sorts = displaySetting.sortOptions(),
                    whereGroups = groupKeys.filterNotNull(),
                )?.first() ?: emptyList()
        when (optionItem) {
            OptionItem.PLAY_NEXT -> addToNextPlay(items)
            OptionItem.ADD_TO_PLAYLIST -> addToPlaylist(items)
            OptionItem.ADD_TO_QUEUE -> addToQueue(items)
            OptionItem.DELETE_MEDIA_FILE -> fileDeleteHelper.deleteMedias(items)
            else -> {}
        }
    }

    context(repository: Repository, popupController: PopupController)
    private suspend fun onShowMusicItemOption(item: AudioItemModel) {
        val options =
            listOf(
                OptionItem.PLAY_NEXT,
                OptionItem.ADD_TO_QUEUE,
                OptionItem.ADD_TO_PLAYLIST,
                OptionItem.OPEN_LIBRARY_ALBUM,
                OptionItem.OPEN_LIBRARY_ARTIST,
                OptionItem.DELETE_MEDIA_FILE,
            )
        val result =
            popupController.showDialog(
                DialogId.OptionDialog(
                    options = options,
                ),
            )
        if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
            when (result.optionItem) {
                OptionItem.PLAY_NEXT -> addToNextPlay(listOf(item))
                OptionItem.ADD_TO_QUEUE -> addToQueue(listOf(item))
                OptionItem.ADD_TO_PLAYLIST -> addToPlaylist(listOf(item))
                OptionItem.OPEN_LIBRARY_ALBUM -> onRequestGoToAlbum(item)
                OptionItem.OPEN_LIBRARY_ARTIST -> onRequestGoToArtist(item)
                OptionItem.DELETE_MEDIA_FILE -> {
                    mediaFileDeleteHelper.deleteMedias(listOf(item))
                }
                else -> {}
            }
        }
    }
}

data class TabContentState(
    val selectedTab: CustomTab? = null,
    val groupSort: DisplaySetting,
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

    data class OnGroupOptionClick(
        val optionItem: OptionItem,
        val groupKeys: List<GroupKey?>,
    ) : TabContentEvent

    data class OnGroupItemClick(
        val groupKeys: List<GroupKey?>,
    ) : TabContentEvent
}
