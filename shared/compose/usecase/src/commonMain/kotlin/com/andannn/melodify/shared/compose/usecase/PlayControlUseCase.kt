/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.shared.compose.popup.DialogHostState
import com.andannn.melodify.shared.compose.popup.entry.alert.AlertDialogAction
import com.andannn.melodify.shared.compose.popup.entry.alert.ChangePlayListAlert

context(repo: Repository, dialogHostState: DialogHostState)
suspend fun playMediaItems(
    mediaItem: MediaItemModel,
    newItems: List<MediaItemModel>,
) {
    val currentPlayListIds = repo.getPlayListQueue().map { it.id }
    val newPlayListIds = newItems.map { it.id }

    val allowPlay =
        if (currentPlayListIds.isNotEmpty() && currentPlayListIds != newPlayListIds) {
            dialogHostState.confirmChangePlayList()
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

private suspend fun DialogHostState.confirmChangePlayList(): Boolean = showDialog(ChangePlayListAlert) == AlertDialogAction.Accept
