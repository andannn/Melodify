/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import com.andannn.melodify.core.data.MediaFileDeleteHelper
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.VideoItemModel
import com.andannn.melodify.shared.compose.common.NavigationRequest
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.model.LibraryDataSource
import com.andannn.melodify.shared.compose.common.model.asLibraryDataSource
import com.andannn.melodify.shared.compose.popup.DialogAction
import com.andannn.melodify.shared.compose.popup.OptionDialog
import com.andannn.melodify.shared.compose.popup.OptionItem
import com.andannn.melodify.shared.compose.popup.PopupController
import com.andannn.melodify.shared.compose.popup.showDialogAndWaitAction
import kotlinx.coroutines.flow.first

/**
 * Show library media options dialog.
 *
 * @param media Media item.
 * @param playListId set playlist id when the [media] is from playlist. Default is null.
 */
context(
    popController: PopupController,
    repository: Repository,
    fileDeleteHelper: MediaFileDeleteHelper,
    eventSink: NavigationRequestEventSink
)
suspend fun showLibraryMediaOption(
    media: MediaItemModel,
    playListId: String? = null,
) {
    val isPlayable = media is AudioItemModel || media is VideoItemModel
    val isAudio = media is AudioItemModel
    val isPlayList = media is PlayListItemModel
    val isFavoritePlayList = media is PlayListItemModel && media.isFavoritePlayList

    suspend fun medias() =
        if (!isPlayable) {
            media.asLibraryDataSource().content().first()
        } else {
            listOf(media)
        }

    val options =
        buildList {
            add(OptionItem.PLAY_NEXT)
            add(OptionItem.ADD_TO_QUEUE)
            add(OptionItem.ADD_TO_PLAYLIST)
            if (isAudio) add(OptionItem.OPEN_LIBRARY_ALBUM)
            if (isAudio) add(OptionItem.OPEN_LIBRARY_ARTIST)
            if (isPlayable) add(OptionItem.DELETE_MEDIA_FILE)
            if (!isPlayable) add(OptionItem.ADD_TO_HOME_TAB)
            if (isPlayList && !isFavoritePlayList) add(OptionItem.DELETE_PLAYLIST)
            if (playListId != null && isPlayable) add(OptionItem.DELETE_FROM_PLAYLIST)
        }
    val result = popController.showDialogAndWaitAction(OptionDialog(options = options))
    if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
        when (result.optionItem) {
            OptionItem.PLAY_NEXT -> {
                addToNextPlay(medias())
            }

            OptionItem.ADD_TO_QUEUE -> {
                addToQueue(medias())
            }

            OptionItem.ADD_TO_HOME_TAB -> {
                media.pinToHomeTab()
            }

            OptionItem.ADD_TO_PLAYLIST -> {
                addToPlaylist(medias() as List<AudioItemModel>)
            }

            OptionItem.DELETE_PLAYLIST -> {
                (media as PlayListItemModel).delete()
            }

            OptionItem.DELETE_MEDIA_FILE -> {
                deleteItems(medias())
            }

            OptionItem.OPEN_LIBRARY_ALBUM -> {
                eventSink.onRequestNavigate(
                    NavigationRequest.GoToLibraryDetail(
                        LibraryDataSource.AlbumDetail(id = (media as AudioItemModel).albumId),
                    ),
                )
            }

            OptionItem.OPEN_LIBRARY_ARTIST -> {
                eventSink.onRequestNavigate(
                    NavigationRequest.GoToLibraryDetail(
                        LibraryDataSource.ArtistDetail(id = (media as AudioItemModel).artistId),
                    ),
                )
            }

            OptionItem.DELETE_FROM_PLAYLIST -> {
                deleteItemInPlayList(
                    playListId = playListId!!,
                    media as AudioItemModel,
                )
            }

            else -> {}
        }
    }
}
