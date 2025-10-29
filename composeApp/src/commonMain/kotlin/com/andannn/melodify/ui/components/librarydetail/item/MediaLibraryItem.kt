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
import com.andannn.melodify.ui.components.librarydetail.showLibraryMediaOption
import com.andannn.melodify.ui.widgets.ListTileItemView
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.launch
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource

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
            is AlbumItemModel -> stringResource(Res.string.track_count, mediaItemModel.trackCount)
            is ArtistItemModel -> stringResource(Res.string.track_count, mediaItemModel.trackCount)
            is AudioItemModel -> mediaItemModel.album
            is GenreItemModel -> stringResource(Res.string.track_count, mediaItemModel.trackCount)
            is PlayListItemModel ->
                stringResource(
                    Res.string.track_count,
                    mediaItemModel.trackCount,
                )
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
                                showLibraryMediaOption(
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
