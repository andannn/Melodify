/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.sortOptions
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.usecase.addToNextPlay
import com.andannn.melodify.usecase.addToPlaylist
import com.andannn.melodify.usecase.addToQueue
import com.andannn.melodify.usecase.contentFlow
import com.andannn.melodify.usecase.deleteItems
import com.andannn.melodify.usecase.pinToHomeTab
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "GroupHeaderPresenter"

data class GroupInfo(
    val groupKey: GroupKey,
    val parentHeaderGroupKey: GroupKey? = null,
    val displaySetting: DisplaySetting?,
    val selectedTab: CustomTab?,
) {
    val selection
        get() = listOf(groupKey, parentHeaderGroupKey)
}

@Composable
fun rememberGroupHeaderPresenter(
    groupInfo: GroupInfo,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
    mediaFileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
): Presenter<GroupHeaderState> =
    retain(
        groupInfo,
        repository,
        popupController,
        mediaFileDeleteHelper,
    ) {
        GroupHeaderPresenter(
            groupInfo,
            repository,
            popupController,
            mediaFileDeleteHelper,
        )
    }

@Stable
data class GroupHeaderState(
    val title: String,
    val cover: String?,
    val trackCount: Int,
    val eventSink: (GroupHeaderEvent) -> Unit = {},
)

sealed interface GroupHeaderEvent {
    data object OnOptionClick : GroupHeaderEvent
}

private class GroupHeaderPresenter(
    private val groupInfo: GroupInfo,
    private val repository: Repository,
    private val popupController: PopupController,
    private val mediaFileDeleteHelper: MediaFileDeleteHelper,
) : ScopedPresenter<GroupHeaderState>() {
    private val mediaContentRepository = repository.mediaContentRepository

    private var mediaItem by mutableStateOf<MediaItemModel?>(null)

    init {
        launch {
            mediaItem =
                when (val groupKey = groupInfo.groupKey) {
                    is GroupKey.Artist -> mediaContentRepository.getArtistByArtistId(artistId = groupKey.artistId)
                    is GroupKey.Album -> mediaContentRepository.getAlbumByAlbumId(albumId = groupKey.albumId)
                    is GroupKey.Genre -> mediaContentRepository.getGenreByGenreId(genreId = groupKey.genreId)
                    else -> null
                }
        }
    }

    @Composable
    override fun present(): GroupHeaderState {
        val groupKey = groupInfo.groupKey
        Napier.d(tag = TAG) { "GroupHeaderPresenter present $groupKey" }
        val title =
            remember(mediaItem, groupKey) {
                when (groupKey) {
                    is GroupKey.Title -> "# " + groupKey.firstCharacterString
                    is GroupKey.Year -> "# " + groupKey.year
                    is GroupKey.BucketId -> "# " + groupKey.bucketDisplayName
                    else -> mediaItem?.name ?: ""
                }
            }

        return GroupHeaderState(
            title = title,
            cover = mediaItem?.artWorkUri,
            trackCount = mediaItem?.trackCount ?: 0,
        ) { event ->
            when (event) {
                GroupHeaderEvent.OnOptionClick -> {
                    launch {
                        val dialog =
                            DialogId.OptionDialog(
                                options =
                                    buildList {
                                        if (groupKey.canPinToHome()) add(OptionItem.ADD_TO_HOME_TAB)
                                        add(OptionItem.PLAY_NEXT)
                                        add(OptionItem.ADD_TO_QUEUE)
                                        add(OptionItem.ADD_TO_PLAYLIST)
                                        add(OptionItem.DELETE_MEDIA_FILE)
                                    },
                            )
                        val result = popupController.showDialog(dialog)
                        if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
                            context(repository, popupController, mediaFileDeleteHelper) {
                                when (result.optionItem) {
                                    OptionItem.ADD_TO_HOME_TAB -> launch { mediaItem?.pinToHomeTab() }
                                    OptionItem.PLAY_NEXT,
                                    OptionItem.ADD_TO_QUEUE,
                                    OptionItem.ADD_TO_PLAYLIST,
                                    OptionItem.DELETE_MEDIA_FILE,
                                    ->
                                        launch {
                                            handleGroupOption(
                                                result.optionItem,
                                                groupInfo.selection,
                                                groupInfo.displaySetting,
                                                groupInfo.selectedTab,
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

    context(_: Repository, _: PopupController, _: MediaFileDeleteHelper)
    private suspend fun handleGroupOption(
        optionItem: OptionItem,
        groupKeys: List<GroupKey?>,
        displaySetting: DisplaySetting?,
        selectedTab: CustomTab?,
    ) {
        val items =
            selectedTab
                ?.contentFlow(
                    sorts = displaySetting?.sortOptions() ?: return,
                    whereGroups = groupKeys.filterNotNull(),
                )?.first() ?: emptyList()
        when (optionItem) {
            OptionItem.PLAY_NEXT -> addToNextPlay(items)
// TODO: Video Playlist impl
            OptionItem.ADD_TO_PLAYLIST -> addToPlaylist(items as List<AudioItemModel>)
            OptionItem.ADD_TO_QUEUE -> addToQueue(items)
            OptionItem.DELETE_MEDIA_FILE -> deleteItems(items)
            else -> {}
        }
    }
}

private fun GroupKey.canPinToHome() =
    when (this) {
        is GroupKey.Album,
        is GroupKey.Artist,
        is GroupKey.Genre,
        -> true

        is GroupKey.Title,
        is GroupKey.Year,
        is GroupKey.BucketId,
        -> false
    }
