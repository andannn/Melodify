/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.mediaitem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.VideoItemModel
import com.andannn.melodify.ui.core.LocalNavigationRequestEventSink
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.NavigationRequestEventSink
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import com.andannn.melodify.ui.widgets.ListTileItemView
import com.andannn.melodify.usecase.showLibraryMediaOption
import kotlinx.coroutines.launch
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun MediaLibraryItem(
    modifier: Modifier = Modifier,
    mediaItemModel: MediaItemModel,
    playListId: String? = null,
    onItemClick: () -> Unit = {},
) {
    val presenter =
        retainMediaLibraryItemPresenter(mediaItemModel, playListId)
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

            is VideoItemModel -> mediaItemModel.bucketName
        }

    ListTileItemView(
        modifier = modifier,
        title = title,
        thumbnailSourceUri = cover,
        subTitle = subTitle,
        defaultColor = Color.Transparent,
        onOptionButtonClick = onOptionButtonClick,
        onItemClick = onItemClick,
    )
}

@Composable
private fun retainMediaLibraryItemPresenter(
    mediaItemModel: MediaItemModel,
    playListId: String?,
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
    popupController: PopupController = LocalPopupController.current,
    repository: Repository = LocalRepository.current,
    fileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
) = retainPresenter(
    mediaItemModel,
    playListId,
    navigationRequestEventSink,
    popupController,
    repository,
    fileDeleteHelper,
) {
    MediaLibraryItemPresenter(
        mediaItemModel = mediaItemModel,
        playListId = playListId,
        popupController = popupController,
        repository = repository,
        fileDeleteHelper = fileDeleteHelper,
        navigationRequestEventSink = navigationRequestEventSink,
    )
}

private class MediaLibraryItemPresenter(
    private val mediaItemModel: MediaItemModel,
    private val playListId: String?,
    private val popupController: PopupController,
    private val repository: Repository,
    private val fileDeleteHelper: MediaFileDeleteHelper,
    private val navigationRequestEventSink: NavigationRequestEventSink,
) : ScopedPresenter<UiState>() {
    @Composable
    override fun present(): UiState =
        UiState { event ->
            context(popupController, repository, fileDeleteHelper, navigationRequestEventSink) {
                when (event) {
                    UiEvent.OnOptionButtonClick ->
                        retainedScope.launch {
                            showLibraryMediaOption(
                                media = mediaItemModel,
                                playListId = playListId,
                            )
                        }
                }
            }
        }
}

@Stable
private data class UiState(
    val eventSink: (UiEvent) -> Unit = {},
)

private sealed interface UiEvent {
    data object OnOptionButtonClick : UiEvent
}
