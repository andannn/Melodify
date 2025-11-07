/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.retain.retain
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.sortOptions
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.LibraryDataSource
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.ui.core.ChannelNavigationRequestEventChannel
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.NavigationRequest
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedObserver
import com.andannn.melodify.ui.core.ScopedObserverImpl
import com.andannn.melodify.usecase.addToNextPlay
import com.andannn.melodify.usecase.addToPlaylist
import com.andannn.melodify.usecase.addToQueue
import com.andannn.melodify.usecase.contentFlow
import com.andannn.melodify.usecase.contentPagingDataFlow
import com.andannn.melodify.usecase.deleteItems
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "TabContentPresenter"

@Composable
fun rememberTabContentPresenter(
    selectedTab: CustomTab?,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
    fileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
) = retain(
    selectedTab,
    repository,
    popupController,
    fileDeleteHelper,
) {
    TabContentPresenter(
        selectedTab = selectedTab,
        repository = repository,
        popupController = popupController,
        mediaFileDeleteHelper = fileDeleteHelper,
    )
}

class TabContentPresenter(
    private val selectedTab: CustomTab?,
    private val repository: Repository,
    private val popupController: PopupController,
    private val mediaFileDeleteHelper: MediaFileDeleteHelper,
    private val scopedObserver: ScopedObserver = ScopedObserverImpl(),
) : Presenter<TabContentState>,
    ScopedObserver by scopedObserver,
    NavigationRequestEventSink by ChannelNavigationRequestEventChannel(scopedObserver) {
    private val mediaControllerRepository = repository.mediaControllerRepository
    private val playListRepository = repository.playListRepository
    private val userPreferenceRepository = repository.userPreferenceRepository

    private var displaySetting =
        userPreferenceRepository.getCurrentSortRule(selectedTab).stateIn(
            scope = this,
            started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
            initialValue = null,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pagingDataFlow =
        displaySetting
            .filterNotNull()
            .flatMapLatest { displaySetting ->
                getContentPagingFlow(selectedTab, displaySetting)
            }.cachedIn(this)

    @Composable
    override fun present(): TabContentState {
        val pagingItems: LazyPagingItems<AudioItemModel> = pagingDataFlow.collectAsLazyPagingItems()
        val displaySettingState by displaySetting.collectAsStateWithLifecycle()
        return TabContentState(
            selectedTab = selectedTab,
            groupSort = displaySettingState,
            pagingItems = pagingItems,
        ) { eventSink ->
            context(repository, popupController, mediaFileDeleteHelper) {
                when (eventSink) {
                    is TabContentEvent.OnPlayMusic ->
                        launch {
                            val items =
                                with(repository) {
                                    selectedTab
                                        ?.contentFlow(sorts = displaySetting.value!!.sortOptions())
                                        ?.first()
                                        ?: error("selectedTab is null")
                                }
                            playMusic(
                                eventSink.mediaItemModel,
                                allAudios = items,
                            )
                        }

                    is TabContentEvent.OnShowMusicItemOption ->
                        launch {
                            onShowMusicItemOption(eventSink.mediaItemModel)
                        }

                    is TabContentEvent.OnGroupItemClick ->
                        launch {
                            handleGroupItemClick(
                                eventSink.groupKeys,
                                displaySetting.value!!,
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

    context(_: Repository, popupController: PopupController, _: MediaFileDeleteHelper)
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
        val result = popupController.showDialog(DialogId.OptionDialog(options = options))

        if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
            when (result.optionItem) {
                OptionItem.PLAY_NEXT -> addToNextPlay(listOf(item))
                OptionItem.ADD_TO_QUEUE -> addToQueue(listOf(item))
                OptionItem.ADD_TO_PLAYLIST -> addToPlaylist(listOf(item))
                OptionItem.DELETE_MEDIA_FILE -> deleteItems(listOf(item))
                OptionItem.OPEN_LIBRARY_ALBUM ->
                    onRequest(
                        NavigationRequest.GoToLibraryDetail(
                            LibraryDataSource.AlbumDetail(id = item.albumId),
                        ),
                    )

                OptionItem.OPEN_LIBRARY_ARTIST ->
                    onRequest(
                        NavigationRequest.GoToLibraryDetail(
                            LibraryDataSource.ArtistDetail(id = item.artistId),
                        ),
                    )

                else -> {}
            }
        }
    }
}

@Stable
data class TabContentState(
    val selectedTab: CustomTab? = null,
    val groupSort: DisplaySetting?,
    val pagingItems: LazyPagingItems<AudioItemModel>,
    val eventSink: (TabContentEvent) -> Unit = {},
)

sealed interface TabContentEvent {
    data class OnShowMusicItemOption(
        val mediaItemModel: AudioItemModel,
    ) : TabContentEvent

    data class OnPlayMusic(
        val mediaItemModel: AudioItemModel,
    ) : TabContentEvent

    data class OnGroupItemClick(
        val groupKeys: List<GroupKey?>,
    ) : TabContentEvent
}
