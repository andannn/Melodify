/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.AudioTrackStyle
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.TabSortRule
import com.andannn.melodify.domain.model.sortOptions
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequest
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.entry.option.MediaOptionDialogResult
import com.andannn.melodify.shared.compose.popup.entry.option.OptionItem
import com.andannn.melodify.shared.compose.popup.entry.option.OptionPopup
import com.andannn.melodify.shared.compose.popup.snackbar.LocalSnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController
import com.andannn.melodify.shared.compose.usecase.addToNextPlay
import com.andannn.melodify.shared.compose.usecase.addToPlaylist
import com.andannn.melodify.shared.compose.usecase.addToQueue
import com.andannn.melodify.shared.compose.usecase.contentFlow
import com.andannn.melodify.shared.compose.usecase.contentPagingDataFlow
import com.andannn.melodify.shared.compose.usecase.deleteItems
import com.andannn.melodify.shared.compose.usecase.playMediaItems
import io.github.aakira.napier.Napier
import io.github.andannn.popup.PopupHostState
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
fun retainTabContentPresenter(
    selectedTab: Tab?,
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
    repository: Repository = LocalRepository.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
    snackBarController: SnackBarController = LocalSnackBarController.current,
    fileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
) = retainPresenter(
    selectedTab,
    navigationRequestEventSink,
    repository,
    popupHostState,
    fileDeleteHelper,
) {
    TabContentPresenter(
        selectedTab = selectedTab,
        navigationRequestEventSink = navigationRequestEventSink,
        repository = repository,
        popupHostState = popupHostState,
        snackBarController = snackBarController,
        mediaFileDeleteHelper = fileDeleteHelper,
    )
}

@Stable
data class TabContentState(
    val selectedTab: Tab? = null,
    val audioTrackStyle: AudioTrackStyle?,
    val tabSortRule: TabSortRule?,
    val pagingItems: LazyPagingItems<MediaItemModel>,
    val eventSink: (TabContentEvent) -> Unit = {},
)

sealed interface TabContentEvent {
    data class OnShowMediaItemOption(
        val mediaItemModel: MediaItemModel,
    ) : TabContentEvent

    data class OnPlayMedia(
        val mediaItemModel: MediaItemModel,
    ) : TabContentEvent

    data class OnGroupItemClick(
        val groupKeys: List<GroupKey?>,
    ) : TabContentEvent
}

private class TabContentPresenter(
    private val selectedTab: Tab?,
    private val navigationRequestEventSink: NavigationRequestEventSink,
    private val repository: Repository,
    private val popupHostState: PopupHostState,
    private val snackBarController: SnackBarController,
    private val mediaFileDeleteHelper: MediaFileDeleteHelper,
) : RetainedPresenter<TabContentState>() {
    private val displaySetting =
        getDisplaySettingFlow().stateInRetainedModel(
            initialValue = null,
        )

    private val audioTrackStyleFlow =
        getAudioTrackStyleFlow().stateInRetainedModel(
            initialValue = null,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pagingDataFlow: Flow<PagingData<MediaItemModel>> =
        displaySetting
            .filterNotNull()
            .flatMapLatest { displaySetting ->
                getContentPagingFlow(selectedTab, displaySetting)
            }.cachedIn(retainedScope)

    @Composable
    override fun present(): TabContentState {
        val pagingItems: LazyPagingItems<MediaItemModel> = pagingDataFlow.collectAsLazyPagingItems()
        val displaySettingState by displaySetting.collectAsStateWithLifecycle()
        val audioTrackStyle by audioTrackStyleFlow.collectAsStateWithLifecycle()
        return TabContentState(
            selectedTab = selectedTab,
            audioTrackStyle = audioTrackStyle,
            tabSortRule = displaySettingState,
            pagingItems = pagingItems,
        ) { eventSink ->
            context(repository, snackBarController, popupHostState, mediaFileDeleteHelper) {
                when (eventSink) {
                    is TabContentEvent.OnPlayMedia -> {
                        retainedScope.launch {
                            val items =
                                with(repository) {
                                    selectedTab
                                        ?.contentFlow(sorts = displaySetting.value!!.sortOptions())
                                        ?.first()
                                        ?: error("selectedTab is null")
                                }
                            playMedia(
                                eventSink.mediaItemModel,
                                items = items,
                            )
                        }
                    }

                    is TabContentEvent.OnShowMediaItemOption -> {
                        retainedScope.launch {
                            onShowMediaItemOption(eventSink.mediaItemModel)
                        }
                    }

                    is TabContentEvent.OnGroupItemClick -> {
                        retainedScope.launch {
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
    }

    private fun getContentPagingFlow(
        selectedTab: Tab?,
        groupSort: TabSortRule,
    ): Flow<PagingData<MediaItemModel>> {
        if (selectedTab == null) {
            return flowOf()
        }
        return with(repository) {
            selectedTab.contentPagingDataFlow(
                sorts = groupSort.sortOptions(),
            ) as Flow<PagingData<MediaItemModel>>
        }
    }

    private fun getDisplaySettingFlow() =
        if (selectedTab != null) {
            repository.getCurrentSortRule(selectedTab)
        } else {
            flowOf(null)
        }

    private fun getAudioTrackStyleFlow() =
        if (selectedTab != null) {
            repository.getAudioTrackStyleFlow(selectedTab)
        } else {
            flowOf(null)
        }

    private fun playMedia(
        mediaItem: MediaItemModel,
        items: List<MediaItemModel>,
    ) {
        retainedScope.launch {
            context(repository, popupHostState) {
                playMediaItems(
                    mediaItem,
                    items,
                )
            }
        }
    }

    context(repository: Repository, popupHostState: PopupHostState)
    private suspend fun handleGroupItemClick(
        groupKeys: List<GroupKey?>,
        tabSortRule: TabSortRule,
        selectedTab: Tab?,
    ) {
        val items =
            selectedTab
                ?.contentFlow(
                    sorts = tabSortRule.sortOptions(),
                    whereGroups = groupKeys.filterNotNull(),
                )?.first() ?: emptyList()
        if (items.isNotEmpty()) {
            playMedia(
                items.first(),
                items,
            )
        }
    }

    context(_: Repository, popupHostState: PopupHostState, _: MediaFileDeleteHelper, snackBarController: SnackBarController)
    private suspend fun onShowMediaItemOption(item: MediaItemModel) {
        Napier.d(message = "onShowMusicItemOption: $item")
        val isAudio = item is AudioItemModel
        val options =
            buildList {
                add(OptionItem.PLAY_NEXT)
                add(OptionItem.ADD_TO_QUEUE)
                add(OptionItem.ADD_TO_PLAYLIST)
                if (isAudio) add(OptionItem.OPEN_LIBRARY_ALBUM)
                if (isAudio) add(OptionItem.OPEN_LIBRARY_ARTIST)
                add(OptionItem.DELETE_MEDIA_FILE)
            }
        val result = popupHostState.showDialog(OptionPopup(options = options))

        if (result is MediaOptionDialogResult.ClickOptionItemResult) {
            when (result.optionItem) {
                OptionItem.PLAY_NEXT -> {
                    addToNextPlay(listOf(item))
                }

                OptionItem.ADD_TO_QUEUE -> {
                    addToQueue(listOf(item))
                }

                OptionItem.ADD_TO_PLAYLIST -> {
                    addToPlaylist(listOf(item))
                }

                OptionItem.DELETE_MEDIA_FILE -> {
                    deleteItems(listOf(item))
                }

                OptionItem.OPEN_LIBRARY_ALBUM -> {
                    navigationRequestEventSink.onRequestNavigate(
                        NavigationRequest.GoToLibraryDetail(
                            LibraryDataSource.AlbumDetail(id = (item as AudioItemModel).albumId),
                        ),
                    )
                }

                OptionItem.OPEN_LIBRARY_ARTIST -> {
                    navigationRequestEventSink.onRequestNavigate(
                        NavigationRequest.GoToLibraryDetail(
                            LibraryDataSource.ArtistDetail(id = (item as AudioItemModel).artistId),
                        ),
                    )
                }

                else -> {}
            }
        }
    }
}
