/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.GroupSort
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.contentFlow
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.handleMediaOptionClick
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    init {
        Napier.d(tag = TAG) { "TabContentPresenter init $selectedTab" }
    }

    @Composable
    override fun present(): TabContentState {
        val scope = rememberCoroutineScope()
        Napier.d(tag = TAG) { "TabContentPresenter scope ${scope.hashCode()}" }

        val listState = rememberLazyListState()
        val groupSort by rememberRetained {
            mutableStateOf(
                GroupSort.Album.TrackNumber(albumAscending = true, trackNumAscending = true),
//                GroupSort.Title(titleAscending = true),
//                GroupSort.Artist.Title(
//                    artistAscending = true,
//                    titleAscending = true,
//                ),
            )
        }

        val contentGroup =
            rememberRetained {
                mutableStateListOf<ContentGroup>()
            }

        LaunchedEffect(groupSort, selectedTab) {
            getContentFlow(selectedTab, groupSort).collect { audioList ->
                contentGroup.clear()
                contentGroup.addAll(audioList.toGroup(groupSort.toSortType()))
                listState.requestScrollToItem(0)
            }
        }

        Napier.d(tag = TAG) { "TabContentPresenter contentMap ${contentGroup.size}" }

        DisposableEffect(Unit) {
            onDispose {
                Napier.d(tag = TAG) { "TabContentPresenter onDispose $selectedTab" }
            }
        }

        return TabContentState(
            selectedTab = selectedTab,
            listState = listState,
            contentGroup = contentGroup.toImmutableList(),
        ) { eventSink ->
            when (eventSink) {
                is TabContentEvent.OnPlayMusic ->
                    scope.launch {
                        playMusic(
                            eventSink.mediaItemModel,
                            allAudios = contentGroup.map { it.content }.flatten(),
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

    private fun getContentFlow(
        selectedTab: CustomTab?,
        groupSort: GroupSort,
    ): Flow<List<AudioItemModel>> {
        if (selectedTab == null) {
            return flow { emit(emptyList()) }
        }
        return with(repository) {
            selectedTab.contentFlow(
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

    private suspend fun onShowMusicItemOption(mediaItemModel: MediaItemModel) {
        val currentTab = selectedTab
        val result =
            if (mediaItemModel is AudioItemModel && currentTab is CustomTab.PlayListDetail) {
                popupController.showDialog(
                    DialogId.AudioOptionInPlayList(
                        playListId = currentTab.playListId,
                        mediaItemModel,
                    ),
                )
            } else {
                popupController.showDialog(
                    DialogId.MediaOption.fromMediaModel(
                        item = mediaItemModel,
                    ),
                )
            }
        if (result is DialogAction.MediaOptionDialog.ClickItem) {
            with(repository) {
                with(popupController) {
                    handleMediaOptionClick(
                        optionItem = result.optionItem,
                        dialog = result.dialog,
                    )
                }
            }
        }
    }
}

enum class GroupType {
    ARTIST,
    ALBUM,
    TITLE,
    NONE,
}

sealed class HeaderItem(
    open val groupType: GroupType,
) {
    data class ID(
        val id: String,
        override val groupType: GroupType,
    ) : HeaderItem(groupType)

    data class Name(
        val name: String,
        override val groupType: GroupType,
    ) : HeaderItem(groupType)
}

data class TabContentState(
    val selectedTab: CustomTab? = null,
    val contentGroup: ImmutableList<ContentGroup> = persistentListOf(),
    val listState: LazyListState,
    val eventSink: (TabContentEvent) -> Unit = {},
) : CircuitUiState

sealed interface TabContentEvent {
    data class OnShowMusicItemOption(
        val mediaItemModel: MediaItemModel,
    ) : TabContentEvent

    data class OnPlayMusic(
        val mediaItemModel: AudioItemModel,
    ) : TabContentEvent
}

data class ContentGroup(
    val headerItem: HeaderItem?,
    val content: List<AudioItemModel>,
)

private fun List<AudioItemModel>.toGroup(groupType: GroupType): List<ContentGroup> =
    this
        .groupBy {
            it.keyOf(groupType)
        }.map { (key, value) ->
            ContentGroup(
                headerItem = groupType.toHeader(key),
                content = value,
            )
        }

private fun GroupType.toHeader(key: String?): HeaderItem? =
    when (this) {
        GroupType.ARTIST ->
            HeaderItem.ID(
                id = key ?: error("key is null"),
                groupType = this,
            )

        GroupType.ALBUM ->
            HeaderItem.ID(
                id = key ?: error("key is null"),
                groupType = this,
            )

        GroupType.TITLE ->
            HeaderItem.Name(
                name = key.toString(),
                groupType = this,
            )

        GroupType.NONE -> null
    }

private fun AudioItemModel.keyOf(groupType: GroupType) =
    when (groupType) {
        GroupType.ARTIST -> artistId
        GroupType.ALBUM -> albumId
        GroupType.TITLE -> name[0].toString()
        GroupType.NONE -> null
    }

private fun GroupSort.toSortType() =
    when (this) {
        is GroupSort.Album -> GroupType.ALBUM
        is GroupSort.Title -> GroupType.TITLE
        is GroupSort.Artist -> GroupType.ARTIST
        GroupSort.NONE -> GroupType.NONE
    }
