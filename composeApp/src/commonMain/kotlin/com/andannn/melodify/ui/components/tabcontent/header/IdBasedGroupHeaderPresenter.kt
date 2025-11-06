/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.GroupKey
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.ScopedPresenter
import com.slack.circuit.runtime.CircuitUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

private const val TAG = "GroupHeaderPresenter"

@Composable
fun rememberGroupHeaderPresenter(
    groupKey: GroupKey,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
//    onGroupOption: (OptionItem) -> Unit = {},
): Presenter<GroupHeaderState> =
    retain(
        groupKey,
        repository,
        popupController,
//        onGroupOption,
    ) {
        GroupHeaderPresenter(
            groupKey,
            repository,
            popupController,
//            onGroupOption,
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
    private val onGroupOption: (OptionItem) -> Unit = {},
) : ScopedPresenter<GroupHeaderState>() {
    private val mediaContentRepository = repository.mediaContentRepository

    private var mediaItem by mutableStateOf<MediaItemModel?>(null)

    init {
        launch {
            mediaItem =
                when (groupKey) {
                    is GroupKey.Artist -> mediaContentRepository.getArtistByArtistId(artistId = groupKey.artistId)
                    is GroupKey.Album -> mediaContentRepository.getAlbumByAlbumId(albumId = groupKey.albumId)
                    is GroupKey.Genre -> mediaContentRepository.getGenreByGenreId(genreId = groupKey.genreId)
                    else -> null
                }
        }
    }

    @Composable
    override fun present(): GroupHeaderState {
        Napier.d(tag = TAG) { "GroupHeaderPresenter present $groupKey" }
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
                            context(repository, popupController) {
                                when (result.optionItem) {
// TODO
//                                    OptionItem.ADD_TO_HOME_TAB -> mediaItem?.pinToHomeTab()
                                    OptionItem.PLAY_NEXT -> onGroupOption(result.optionItem)
                                    OptionItem.ADD_TO_QUEUE -> onGroupOption(result.optionItem)
                                    OptionItem.ADD_TO_PLAYLIST -> onGroupOption(result.optionItem)
                                    OptionItem.DELETE_MEDIA_FILE -> onGroupOption(result.optionItem)
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
