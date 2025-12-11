/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.usecase

import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.showDialogAndWaitAction

context(repo: Repository, popupController: PopupController)
suspend fun playMediaItems(
    mediaItem: MediaItemModel,
    newItems: List<MediaItemModel>,
) {
    val currentPlayListIds = repo.getPlayListQueue().map { it.id }
    val newPlayListIds = newItems.map { it.id }

    val allowPlay =
        if (currentPlayListIds.isNotEmpty() && currentPlayListIds != newPlayListIds) {
            popupController.confirmChangePlayList()
        } else {
            // playlist is not change, just play the media item
            true
        }

    if (allowPlay) {
        repo.playMediaList(
            newItems.toList(),
            newItems.indexOf(mediaItem),
        )
    }
}

private suspend fun PopupController.confirmChangePlayList(): Boolean {
    val result = showDialogAndWaitAction(DialogId.ChangePlayListAlert)
    return result == DialogAction.AlertDialog.Accept
}
