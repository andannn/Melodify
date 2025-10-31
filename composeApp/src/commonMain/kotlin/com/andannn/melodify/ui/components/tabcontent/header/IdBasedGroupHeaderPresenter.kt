/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.usecase.pinToHomeTab
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

private const val TAG = "GroupHeaderPresenter"

@Composable
fun rememberGroupHeaderPresenter(
    groupKey: GroupKey,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
): Presenter<GroupHeaderState> =
    remember(
        groupKey,
        repository,
        popupController,
    ) {
        GroupHeaderPresenter(
            groupKey,
            repository,
            popupController,
        )
    }

data class GroupHeaderState(
    val title: String,
    val cover: String?,
    val trackCount: Int,
    val eventSink: (GroupHeaderEvent) -> Unit = {},
) : CircuitUiState

sealed interface GroupHeaderEvent {
    data object OnOptionClick : GroupHeaderEvent
}

private class GroupHeaderPresenter(
    private val groupKey: GroupKey,
    private val repository: Repository,
    private val popupController: PopupController,
) : Presenter<GroupHeaderState> {
    private val mediaContentRepository = repository.mediaContentRepository

    @Composable
    override fun present(): GroupHeaderState {
        Napier.d(tag = TAG) { "GroupHeaderPresenter present $groupKey" }
        val scope = rememberCoroutineScope()
        val mediaItem by produceRetainedState<MediaItemModel?>(null) {
            value =
                when (groupKey) {
                    is GroupKey.Artist -> mediaContentRepository.getArtistByArtistId(artistId = groupKey.artistId)
                    is GroupKey.Album -> mediaContentRepository.getAlbumByAlbumId(albumId = groupKey.albumId)
                    is GroupKey.Genre -> mediaContentRepository.getGenreByGenreId(genreId = groupKey.genreId)
                    else -> null
                }
        }

        val title =
            remember(mediaItem, groupKey) {
                when (groupKey) {
                    is GroupKey.Title -> "# " + groupKey.firstCharacterString
                    is GroupKey.Year -> "# " + groupKey.year
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
                    scope.launch {
                        val dialog =
                            DialogId.OptionDialog(
                                options =
                                    buildList {
                                        if (groupKey.canPinToHome()) add(OptionItem.ADD_TO_HOME_TAB)
                                        add(OptionItem.PLAY_NEXT)
                                        add(OptionItem.ADD_TO_QUEUE)
                                        add(OptionItem.ADD_TO_PLAYLIST)
                                    },
                            )
                        val result = popupController.showDialog(dialog)
                        if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
                            context(repository, popupController) {
                                when (result.optionItem) {
                                    OptionItem.ADD_TO_HOME_TAB -> mediaItem?.pinToHomeTab()
                                    OptionItem.PLAY_NEXT -> {}
                                    OptionItem.ADD_TO_QUEUE -> {}
                                    OptionItem.ADD_TO_PLAYLIST -> {}
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

private fun GroupKey.canPinToHome() =
    when (this) {
        is GroupKey.Album,
        is GroupKey.Artist,
        is GroupKey.Genre,
        -> true

        is GroupKey.Title,
        is GroupKey.Year,
        -> false
    }
