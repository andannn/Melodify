/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import com.andannn.melodify.MediaFileDeleteHelper
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.model.OptionItem
import com.andannn.melodify.model.asLibraryDataSource
import com.andannn.melodify.ui.core.PopupController
import kotlinx.coroutines.flow.first

/**
 * Show library media options dialog.
 *
 * @param media Media item.
 * @param playListId set playlist id when the [media] is from playlist. Default is null.
 */
context(popController: PopupController, repository: Repository, fileDeleteHelper: MediaFileDeleteHelper)
suspend fun showLibraryMediaOption(
    media: MediaItemModel,
    playListId: String? = null,
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
            if (isAudio) add(OptionItem.DELETE_MEDIA_FILE)
            if (!isAudio) add(OptionItem.ADD_TO_HOME_TAB)
            if (isPlayList && !isFavoritePlayList) add(OptionItem.DELETE_PLAYLIST)
            if (playListId != null && isAudio) add(OptionItem.DELETE_FROM_PLAYLIST)
        }
    val result = popController.showDialog(DialogId.OptionDialog(options = options))
    if (result is DialogAction.MediaOptionDialog.ClickOptionItem) {
        when (result.optionItem) {
            OptionItem.PLAY_NEXT -> addToNextPlay(medias())
            OptionItem.ADD_TO_QUEUE -> addToQueue(medias())
            OptionItem.ADD_TO_HOME_TAB -> media.pinToHomeTab()
            OptionItem.ADD_TO_PLAYLIST -> addToPlaylist(medias())
            OptionItem.DELETE_PLAYLIST -> (media as PlayListItemModel).delete()
            OptionItem.DELETE_MEDIA_FILE -> deleteItems(medias())
            OptionItem.DELETE_FROM_PLAYLIST ->
                deleteItemInPlayList(
                    playListId = playListId!!,
                    media as AudioItemModel,
                )

            else -> {}
        }
    }
}
