/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.library.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AlbumItemModel
import com.andannn.melodify.domain.model.ArtistItemModel
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GenreItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import com.andannn.melodify.shared.compose.popup.LocalPopupController
import com.andannn.melodify.shared.compose.popup.PopupController
import com.andannn.melodify.shared.compose.popup.snackbar.LocalSnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController
import com.andannn.melodify.shared.compose.usecase.showLibraryMediaOption
import kotlinx.coroutines.launch
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.track_count
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
            is AlbumItemModel -> {
                stringResource(Res.string.track_count, mediaItemModel.trackCount)
            }

            is ArtistItemModel -> {
                stringResource(Res.string.track_count, mediaItemModel.trackCount)
            }

            is AudioItemModel -> {
                mediaItemModel.album
            }

            is GenreItemModel -> {
                ""
            }

            is PlayListItemModel -> {
                stringResource(
                    Res.string.track_count,
                    mediaItemModel.trackCount,
                )
            }

            is VideoItemModel -> {
                mediaItemModel.bucketName
            }
        }

    ListTileItemView(
        modifier = modifier,
        title = title,
        thumbnailSourceUri = cover.takeIf { mediaItemModel is AudioItemModel },
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
    snackBarController: SnackBarController = LocalSnackBarController.current,
    repository: Repository = LocalRepository.current,
    fileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
) = retainPresenter(
    mediaItemModel,
    playListId,
    navigationRequestEventSink,
    popupController,
    snackBarController,
    repository,
    fileDeleteHelper,
) {
    MediaLibraryItemPresenter(
        mediaItemModel = mediaItemModel,
        playListId = playListId,
        popupController = popupController,
        snackBarController = snackBarController,
        repository = repository,
        fileDeleteHelper = fileDeleteHelper,
        navigationRequestEventSink = navigationRequestEventSink,
    )
}

private class MediaLibraryItemPresenter(
    private val mediaItemModel: MediaItemModel,
    private val playListId: String?,
    private val popupController: PopupController,
    private val snackBarController: SnackBarController,
    private val repository: Repository,
    private val fileDeleteHelper: MediaFileDeleteHelper,
    private val navigationRequestEventSink: NavigationRequestEventSink,
) : RetainedPresenter<UiState>() {
    @Composable
    override fun present(): UiState =
        UiState { event ->
            context(popupController, snackBarController, repository, fileDeleteHelper, navigationRequestEventSink) {
                when (event) {
                    UiEvent.OnOptionButtonClick -> {
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
}

@Stable
private data class UiState(
    val eventSink: (UiEvent) -> Unit = {},
)

private sealed interface UiEvent {
    data object OnOptionButtonClick : UiEvent
}
