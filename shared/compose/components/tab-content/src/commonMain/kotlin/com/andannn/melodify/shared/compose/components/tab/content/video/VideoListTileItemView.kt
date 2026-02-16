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
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import com.andannn.melodify.shared.compose.common.widgets.ListTileItemView
import io.github.aakira.napier.Napier
import io.github.andannn.RetainedModel
import io.github.andannn.retainRetainedModel

private const val TAG = "VideoListTileItemView"

@Composable
internal fun VideoListTileItemView(
    modifier: Modifier = Modifier,
    tab: Tab?,
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
            isActive = false,
            thumbnailSourceUri = null,
            title = item.name,
            onItemClick = onItemClick,
            onOptionButtonClick = onOptionButtonClick,
        )

        if (tab != null) {
            val model = retainVideoListTileItemModel(tab)
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
    tab: Tab,
    repository: Repository = LocalRepository.current,
) = retainRetainedModel(
    tab,
    repository,
) {
    VideoListTileItemModel(tab, repository)
}

private class VideoListTileItemModel(
    tab: Tab,
    repository: Repository,
) : RetainedModel() {
    val isShowProgressStateFlow =
        repository.getIsShowVideoProgressFlow(tab).stateInRetainedModel(
            initialValue = false,
        )
}
