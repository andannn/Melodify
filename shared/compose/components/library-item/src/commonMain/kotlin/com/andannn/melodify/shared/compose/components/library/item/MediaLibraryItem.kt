/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.library.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AlbumItemModel
import com.andannn.melodify.domain.model.ArtistItemModel
import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.GenreItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.MediaType
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.widgets.ActionType
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.snackbar.LocalSnackBarController
import com.andannn.melodify.shared.compose.popup.snackbar.SnackBarController
import com.andannn.melodify.shared.compose.usecase.showLibraryMediaOption
import io.github.andannn.RetainedModel
import io.github.andannn.popup.PopupHostState
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.launch
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun MediaLibraryItem(
    contentId: Long,
    contentType: MediaType,
    modifier: Modifier = Modifier,
    showOptions: Boolean = true,
    onItemClick: (MediaItemModel) -> Unit = {},
) {
    val item = retainFindMediaItemPresenter(contentId, contentType).mediaItem.value

    MediaLibraryItem(
        modifier = modifier,
        mediaItemModel = item,
        showOptions = showOptions,
        onItemClick = {
            if (item != null) {
                onItemClick(item)
            }
        },
    )
}

@Composable
private fun retainFindMediaItemPresenter(
    contentId: Long,
    contentType: MediaType,
    repository: Repository = LocalRepository.current,
) = retainRetainedModel(
    contentId,
    contentType,
    repository,
) {
    FindMediaItemRetainedModel(
        contentId = contentId,
        contentType = contentType,
        repository = repository,
    )
}

private class FindMediaItemRetainedModel(
    contentId: Long,
    contentType: MediaType,
    repository: Repository,
) : RetainedModel() {
    val mediaItem = mutableStateOf<MediaItemModel?>(null)

    init {
        retainedScope.launch {
            val item =
                when (contentType) {
                    MediaType.AUDIO -> repository.getAudioById(audioId = contentId)
                    MediaType.VIDEO -> repository.getVideoById(videoId = contentId)
                    MediaType.ALBUM -> repository.getAlbumByAlbumId(albumId = contentId)
                    MediaType.ARTIST -> repository.getArtistByArtistId(artistId = contentId)
                    MediaType.GENRE -> repository.getGenreByGenreId(genreId = contentId)
                    MediaType.PLAYLIST -> repository.getPlayListById(playListId = contentId)
                }
            mediaItem.value = item
        }
    }
}

@Composable
fun MediaLibraryItem(
    modifier: Modifier = Modifier,
    mediaItemModel: MediaItemModel?,
    showOptions: Boolean = true,
    playListId: String? = null,
    onItemClick: () -> Unit = {},
) {
    if (mediaItemModel == null) {
        return MediaLibraryItemContent(
            modifier = modifier,
            mediaItemModel = null,
        )
    }

    val presenter =
        retainMediaLibraryItemPresenter(mediaItemModel, playListId)
    val state = presenter.present()
    MediaLibraryItemContent(
        modifier = modifier,
        mediaItemModel = mediaItemModel,
        showOptions = showOptions,
        onItemClick = onItemClick,
        onOptionButtonClick = {
            state.eventSink.invoke(UiEvent.OnOptionButtonClick)
        },
    )
}

@Composable
private fun MediaLibraryItemContent(
    modifier: Modifier = Modifier,
    mediaItemModel: MediaItemModel?,
    showOptions: Boolean = true,
    onOptionButtonClick: () -> Unit = {},
    onItemClick: () -> Unit = {},
) {
    val title = mediaItemModel?.name ?: ""
    val cover = mediaItemModel?.artWorkUri ?: ""
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

            null -> {
                ""
            }
        }

    ListTileItemView(
        modifier = modifier,
        title = title,
        thumbnailSourceUri = cover.takeIf { mediaItemModel is AudioItemModel },
        subTitle = subTitle,
        defaultColor = Color.Transparent,
        actionType = if (showOptions) ActionType.OPTION else ActionType.NONE,
        onOptionButtonClick = onOptionButtonClick,
        onItemClick = onItemClick,
    )
}

@Composable
private fun retainMediaLibraryItemPresenter(
    mediaItemModel: MediaItemModel,
    playListId: String?,
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
    snackBarController: SnackBarController = LocalSnackBarController.current,
    repository: Repository = LocalRepository.current,
    fileDeleteHelper: MediaFileDeleteHelper = getKoin().get(),
) = retainPresenter(
    mediaItemModel,
    playListId,
    navigationRequestEventSink,
    popupHostState,
    snackBarController,
    repository,
    fileDeleteHelper,
) {
    MediaLibraryItemPresenter(
        mediaItemModel = mediaItemModel,
        playListId = playListId,
        popupHostState = popupHostState,
        snackBarController = snackBarController,
        repository = repository,
        fileDeleteHelper = fileDeleteHelper,
        navigationRequestEventSink = navigationRequestEventSink,
    )
}

private class MediaLibraryItemPresenter(
    private val mediaItemModel: MediaItemModel,
    private val playListId: String?,
    private val popupHostState: PopupHostState,
    private val snackBarController: SnackBarController,
    private val repository: Repository,
    private val fileDeleteHelper: MediaFileDeleteHelper,
    private val navigationRequestEventSink: NavigationRequestEventSink,
) : RetainedPresenter<UiState>() {
    @Composable
    override fun present(): UiState =
        UiState { event ->
            context(
                popupHostState,
                snackBarController,
                repository,
                fileDeleteHelper,
                navigationRequestEventSink,
            ) {
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
