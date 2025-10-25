/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.tabcontent.GroupType
import com.andannn.melodify.ui.components.tabcontent.HeaderItem
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier

private const val TAG = "GroupHeaderPresenter"

@Composable
fun rememberGroupHeaderPresenter(
    headerItem: HeaderItem.ID,
    repository: Repository = LocalRepository.current,
) = remember(
    headerItem,
    repository,
) {
    GroupHeaderPresenter(
        headerItem,
        repository,
    )
}

class GroupHeaderPresenter(
    private val headerItem: HeaderItem.ID,
    repository: Repository,
) : Presenter<GroupHeaderState> {
    private val groupType = headerItem.groupType
    private val headerId = headerItem.id
    private val mediaContentRepository = repository.mediaContentRepository

    @Composable
    override fun present(): GroupHeaderState {
        Napier.d(tag = TAG) { "GroupHeaderPresenter present $headerItem" }
        val mediaItem by produceRetainedState<MediaItemModel?>(null) {
            value =
                when (groupType) {
                    GroupType.ARTIST -> mediaContentRepository.getArtistByArtistId(headerId)
                    GroupType.ALBUM -> mediaContentRepository.getAlbumByAlbumId(headerId)
                    else -> error("invalid group type")
                }
        }

        return GroupHeaderState(
            mediaItem = mediaItem,
            title = mediaItem?.name ?: "",
            cover = mediaItem?.artWorkUri,
            trackCount = mediaItem?.trackCount ?: 0,
        )
    }
}

data class GroupHeaderState(
    val mediaItem: MediaItemModel?,
    val title: String,
    val cover: String?,
    val trackCount: Int,
) : CircuitUiState
