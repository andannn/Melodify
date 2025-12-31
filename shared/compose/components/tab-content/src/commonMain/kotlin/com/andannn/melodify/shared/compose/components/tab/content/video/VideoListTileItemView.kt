/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content.video

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.domain.model.browsableOrPlayable
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import io.github.aakira.napier.Napier
import io.github.andannn.RetainedModel
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

private const val TAG = "VideoListTileItemView"

@Composable
internal fun VideoListTileItemView(
    modifier: Modifier = Modifier,
    tabId: Long?,
    item: VideoItemModel,
    onItemClick: (() -> Unit)? = null,
    onOptionButtonClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
    ) {
        ListTileItemView(
            paddingValues =
                PaddingValues(
                    top = 4.dp,
                    bottom = 4.dp,
                ),
            playable = item.browsableOrPlayable,
            isActive = false,
            thumbnailSourceUri = null,
            title = item.name,
            onItemClick = onItemClick,
            onOptionButtonClick = onOptionButtonClick,
        )

        if (tabId != null) {
            val model = retainVideoListTileItemModel(tabId)
            val isShowProgress by model.isShowProgressStateFlow.collectAsStateWithLifecycle()
            Napier.d(tag = TAG) { "isShowProgress $isShowProgress" }
            if (isShowProgress) {
                VideoPlayBackProgress(item)
            }
        }
    }
}

@Composable
private fun retainVideoListTileItemModel(
    tabId: Long,
    repository: Repository = LocalRepository.current,
) = retainRetainedModel(
    tabId,
    repository,
) {
    VideoListTileItemModel(tabId, repository)
}

private class VideoListTileItemModel(
    tabId: Long,
    repository: Repository,
) : RetainedModel() {
    init {
        Napier.d(tag = TAG) { "init" }
    }

    val isShowProgressStateFlow =
        repository.getIsShowVideoProgressFlow(tabId).stateIn(
            scope = retainedScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false,
        )
}
