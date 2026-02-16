/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.usecase

import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.browsable
import com.andannn.melodify.shared.compose.common.NavigationRequest
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.model.asLibraryDataSource
import com.andannn.melodify.shared.compose.popup.entry.alert.AlertDialogAction
import com.andannn.melodify.shared.compose.popup.entry.alert.ChangePlayListAlert
import io.github.andannn.popup.PopupHostState

context(_: PopupHostState, _: Repository, navigationRequestEventSink: NavigationRequestEventSink)
suspend fun playOrGoToBrowsable(item: MediaItemModel) {
    if (item.browsable) {
        navigationRequestEventSink.onRequestNavigate(
            NavigationRequest.GoToLibraryDetail(item.asLibraryDataSource()),
        )
    } else {
        playMediaItems(
            item,
            listOf(item),
        )
    }
}

context(repo: Repository, popupHostState: PopupHostState)
suspend fun playMediaItems(
    mediaItem: MediaItemModel,
    newItems: List<MediaItemModel>,
) {
    val currentPlayListIds = repo.getPlayListQueue().map { it.id }
    val newPlayListIds = newItems.map { it.id }

    val allowPlay =
        if (currentPlayListIds.isNotEmpty() && currentPlayListIds != newPlayListIds) {
            popupHostState.confirmChangePlayList()
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

private suspend fun PopupHostState.confirmChangePlayList(): Boolean = showDialog(ChangePlayListAlert) == AlertDialogAction.Accept
