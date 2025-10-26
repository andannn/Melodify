/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.dialog.OptionItem
import com.andannn.melodify.ui.components.popup.handleMediaOptionClick
import com.andannn.melodify.ui.components.popup.pinToHomeTab
import com.andannn.melodify.ui.components.tabcontent.GroupType
import com.andannn.melodify.ui.components.tabcontent.HeaderItem
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

private const val TAG = "GroupHeaderPresenter"

@Composable
fun rememberGroupHeaderPresenter(
    headerItem: HeaderItem.ID,
    repository: Repository = LocalRepository.current,
    popupController: PopupController = LocalPopupController.current,
) = remember(
    headerItem,
    repository,
    popupController,
) {
    GroupHeaderPresenter(
        headerItem,
        repository,
        popupController,
    )
}

class GroupHeaderPresenter(
    private val headerItem: HeaderItem.ID,
    private val repository: Repository,
    private val popupController: PopupController,
) : Presenter<GroupHeaderState> {
    private val groupType = headerItem.groupType
    private val headerId = headerItem.id
    private val mediaContentRepository = repository.mediaContentRepository

    @Composable
    override fun present(): GroupHeaderState {
        Napier.d(tag = TAG) { "GroupHeaderPresenter present $headerItem" }
        val scope = rememberCoroutineScope()
        val mediaItem by produceRetainedState<MediaItemModel?>(null) {
            value =
                when (groupType) {
                    GroupType.ARTIST -> mediaContentRepository.getArtistByArtistId(headerId)
                    GroupType.ALBUM -> mediaContentRepository.getAlbumByAlbumId(headerId)
                    else -> error("invalid group type")
                }
        }

        return GroupHeaderState(
            mediaItem = mediaItem,
            title = mediaItem?.name ?: "",
            cover = mediaItem?.artWorkUri,
            trackCount = mediaItem?.trackCount ?: 0,
        ) { event ->
            when (event) {
                GroupHeaderEvent.OnOptionClick -> {
                    scope.launch {
                        val media = mediaItem ?: error("mediaItem is null")
                        val dialog =
                            DialogId.OptionDialog(
                                options =
                                    listOf(
                                        OptionItem.ADD_TO_HOME_TAB,
                                    ),
                            )
                        val result = popupController.showDialog(dialog)
                        if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
                            context(repository, popupController) {
                                when (result.optionItem) {
                                    OptionItem.ADD_TO_HOME_TAB -> media.pinToHomeTab()
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

data class GroupHeaderState(
    val mediaItem: MediaItemModel?,
    val title: String,
    val cover: String?,
    val trackCount: Int,
    val eventSink: (GroupHeaderEvent) -> Unit = {},
) : CircuitUiState

sealed interface GroupHeaderEvent {
    data object OnOptionClick : GroupHeaderEvent
}
