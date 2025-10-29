/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.librarydetail.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.LocalPopupController
import com.andannn.melodify.LocalRepository
import com.andannn.melodify.PopupController
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.asLibraryDataSource
import com.andannn.melodify.ui.popup.dialog.OptionItem
import com.andannn.melodify.ui.widgets.ListTileItemView
import com.andannn.melodify.usecase.addToNextPlay
import com.andannn.melodify.usecase.addToPlaylist
import com.andannn.melodify.usecase.addToQueue
import com.andannn.melodify.usecase.content
import com.andannn.melodify.usecase.delete
import com.andannn.melodify.usecase.deleteItemInPlayList
import com.andannn.melodify.usecase.pinToHomeTab
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun MediaLibraryItem(
    modifier: Modifier = Modifier,
    mediaItemModel: MediaItemModel,
    playListId: String? = null,
    onItemClick: () -> Unit = {},
) {
    val presenter: MediaLibraryItemPresenter =
        rememberMediaLibraryItemPresenter(mediaItemModel, playListId)

    val state = presenter.present()
    MediaLibraryItemContent(
        modifier = modifier,
        mediaItemModel = mediaItemModel,
        onItemClick = onItemClick,
        onOptionButtonClick = {
            state.eventSink.invoke(UiEvent.OnOptionButtonClick)
        },
    )
}

@Composable
private fun MediaLibraryItemContent(
    modifier: Modifier = Modifier,
    mediaItemModel: MediaItemModel,
    onOptionButtonClick: () -> Unit = {},
    onItemClick: () -> Unit = {},
) {
    val title = mediaItemModel.name
    val cover = mediaItemModel.artWorkUri
    val subTitle =
        when (mediaItemModel) {
            is AlbumItemModel -> "Album"
            is ArtistItemModel -> "Artist"
            is AudioItemModel -> "Song"
            is GenreItemModel -> "Genre"
            is PlayListItemModel -> "Playlist"
        }

    ListTileItemView(
        modifier = modifier,
        title = title,
        albumArtUri = cover,
        subTitle = subTitle,
        defaultColor = Color.Transparent,
        onOptionButtonClick = onOptionButtonClick,
        onMusicItemClick = onItemClick,
    )
}

@Composable
private fun rememberMediaLibraryItemPresenter(
    mediaItemModel: MediaItemModel,
    playListId: String?,
    popupController: PopupController = LocalPopupController.current,
    repository: Repository = LocalRepository.current,
) = remember(
    mediaItemModel,
    playListId,
    popupController,
    repository,
) {
    MediaLibraryItemPresenter(
        mediaItemModel = mediaItemModel,
        playListId = playListId,
        popupController = popupController,
        repository = repository,
    )
}

private class MediaLibraryItemPresenter(
    private val mediaItemModel: MediaItemModel,
    private val playListId: String?,
    private val popupController: PopupController,
    private val repository: Repository,
) : Presenter<UiState> {
    @Composable
    override fun present(): UiState {
        val scope = rememberCoroutineScope()
        return UiState { event ->
            with(popupController) {
                with(repository) {
                    when (event) {
                        UiEvent.OnOptionButtonClick ->
                            scope.launch {
                                handleShowMediaOption(
                                    media = mediaItemModel,
                                    playListId = playListId,
                                )
                            }
                    }
                }
            }
        }
    }
}

private data class UiState(
    val eventSink: (UiEvent) -> Unit = {},
) : CircuitUiState

private sealed interface UiEvent {
    data object OnOptionButtonClick : UiEvent
}

context(popController: PopupController, repository: Repository)
private suspend fun handleShowMediaOption(
    media: MediaItemModel,
    playListId: String?,
) {
    val isAudio = media is AudioItemModel
    val isPlayList = media is PlayListItemModel
    val isFavoritePlayList = media is PlayListItemModel && media.isFavorite

    suspend fun medias() =
        if (media !is AudioItemModel) {
            media.asLibraryDataSource().content().first() as List<AudioItemModel>
        } else {
            listOf(media)
        }

    val options =
        buildList {
            add(OptionItem.PLAY_NEXT)
            add(OptionItem.ADD_TO_QUEUE)
            add(OptionItem.ADD_TO_PLAYLIST)
            if (!isAudio) add(OptionItem.ADD_TO_HOME_TAB)
            if (isPlayList && !isFavoritePlayList) add(OptionItem.DELETE_PLAYLIST)
            if (playListId != null && isAudio) add(OptionItem.DELETE_FROM_PLAYLIST)
        }
    val result =
        popController.showDialog(
            DialogId.OptionDialog(
                options = options,
            ),
        )
    if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
        when (result.optionItem) {
            OptionItem.PLAY_NEXT -> addToNextPlay(medias())
            OptionItem.ADD_TO_QUEUE -> addToQueue(medias())
            OptionItem.ADD_TO_HOME_TAB -> media.pinToHomeTab()
            OptionItem.ADD_TO_PLAYLIST -> addToPlaylist(medias())
            OptionItem.DELETE_PLAYLIST -> (media as PlayListItemModel).delete()
            OptionItem.DELETE_FROM_PLAYLIST ->
                deleteItemInPlayList(
                    playListId = playListId!!,
                    media as AudioItemModel,
                )

            else -> {}
        }
    }
}
