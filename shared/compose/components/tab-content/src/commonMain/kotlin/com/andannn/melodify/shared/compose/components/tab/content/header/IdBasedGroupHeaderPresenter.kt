/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.DisplaySetting
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.TabKind
import com.andannn.melodify.domain.model.sortOptions
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
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
import com.andannn.melodify.shared.compose.usecase.deleteItems
import com.andannn.melodify.shared.compose.usecase.pinToHomeTab
import io.github.aakira.napier.Napier
import io.github.andannn.popup.PopupHostState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "GroupHeaderPresenter"

internal data class GroupInfo(
    val groupKey: GroupKey,
    val parentHeaderGroupKey: GroupKey? = null,
    val displaySetting: DisplaySetting?,
    val selectedTab: CustomTab?,
) {
    val selection
        get() = listOf(groupKey, parentHeaderGroupKey)
}

@Composable
internal fun retainGroupHeaderPresenter(
    groupInfo: GroupInfo,
    repository: Repository = LocalRepository.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
    snackBarController: SnackBarController = LocalSnackBarController.current,
    mediaFileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
): Presenter<GroupHeaderState> =
    retainPresenter(
        groupInfo,
        repository,
        popupHostState,
        mediaFileDeleteHelper,
    ) {
        GroupHeaderPresenter(
            groupInfo,
            repository,
            popupHostState,
            snackBarController,
            mediaFileDeleteHelper,
        )
    }

@Stable
internal data class GroupHeaderState(
    val title: String,
    val cover: String?,
    val trackCount: Int,
    val eventSink: (GroupHeaderEvent) -> Unit = {},
)

internal sealed interface GroupHeaderEvent {
    data object OnOptionClick : GroupHeaderEvent
}

private class GroupHeaderPresenter(
    private val groupInfo: GroupInfo,
    private val repository: Repository,
    private val popupHostState: PopupHostState,
    private val snackBarController: SnackBarController,
    private val mediaFileDeleteHelper: MediaFileDeleteHelper,
) : RetainedPresenter<GroupHeaderState>() {
    private var mediaItem by mutableStateOf<MediaItemModel?>(null)

    init {
        retainedScope.launch {
            mediaItem =
                when (val groupKey = groupInfo.groupKey) {
                    is GroupKey.Artist -> repository.getArtistByArtistId(artistId = groupKey.artistId)
                    is GroupKey.Album -> repository.getAlbumByAlbumId(albumId = groupKey.albumId)
                    is GroupKey.Genre -> repository.getGenreByGenreId(genreId = groupKey.genreId)
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
                    retainedScope.launch {
                        val dialog =
                            OptionPopup(
                                options =
                                    buildList {
                                        if (groupKey.canPinToHome()) add(OptionItem.ADD_TO_HOME_TAB)
                                        add(OptionItem.PLAY_NEXT)
                                        add(OptionItem.ADD_TO_QUEUE)
                                        add(OptionItem.ADD_TO_PLAYLIST)
                                        add(OptionItem.DELETE_MEDIA_FILE)
                                    },
                            )
                        val result = popupHostState.showDialog(dialog)
                        if (result is MediaOptionDialogResult.ClickOptionItemResult) {
                            context(
                                repository,
                                snackBarController,
                                popupHostState,
                                mediaFileDeleteHelper,
                            ) {
                                when (result.optionItem) {
                                    OptionItem.ADD_TO_HOME_TAB -> {
                                        launch {
                                            if (groupInfo.groupKey is GroupKey.BucketId) {
                                                pinToHomeTab(
                                                    externalId = groupInfo.groupKey.bucketId,
                                                    tabName = groupInfo.groupKey.bucketDisplayName,
                                                    tabKind = TabKind.VIDEO_BUCKET,
                                                )
                                            } else {
                                                mediaItem?.pinToHomeTab()
                                            }
                                        }
                                    }

                                    OptionItem.PLAY_NEXT,
                                    OptionItem.ADD_TO_QUEUE,
                                    OptionItem.ADD_TO_PLAYLIST,
                                    OptionItem.DELETE_MEDIA_FILE,
                                    -> {
                                        launch {
                                            handleGroupOption(
                                                result.optionItem,
                                                groupInfo.selection,
                                                groupInfo.displaySetting,
                                                groupInfo.selectedTab,
                                            )
                                        }
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

    context(_: Repository, _: PopupHostState, _: SnackBarController, _: MediaFileDeleteHelper)
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
            OptionItem.PLAY_NEXT -> {
                addToNextPlay(items)
            }

            OptionItem.ADD_TO_PLAYLIST -> {
                addToPlaylist(items)
            }

            OptionItem.ADD_TO_QUEUE -> {
                addToQueue(items)
            }

            OptionItem.DELETE_MEDIA_FILE -> {
                deleteItems(items)
            }

            else -> {}
        }
    }
}

private fun GroupKey.canPinToHome() =
    when (this) {
        is GroupKey.Album,
        is GroupKey.Artist,
        is GroupKey.Genre,
        is GroupKey.BucketId,
        -> true

        is GroupKey.Title,
        is GroupKey.Year,
        -> false
    }
