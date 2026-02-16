/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import io.github.aakira.napier.Napier
import io.github.andannn.RetainedModel
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.flow.map

private const val TAG = "VideoPlayBackProgress"

@Composable
internal fun VideoPlayBackProgress(
    item: VideoItemModel,
    modifier: Modifier = Modifier,
) {
    val model = retainVideoPlayBackProgressModel(item)
    val progress by model.progressFactorStateFlow.collectAsStateWithLifecycle()
    Napier.d(tag = TAG) { "init progress $progress" }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().padding(end = 48.dp),
            progress = { progress },
            drawStopIndicator = {},
        )
    }
}

@Composable
private fun retainVideoPlayBackProgressModel(
    item: VideoItemModel,
    repository: Repository = LocalRepository.current,
) = retainRetainedModel(item) {
    VideoPlayBackProgressModel(item, repository)
}

private class VideoPlayBackProgressModel(
    item: VideoItemModel,
    repository: Repository,
) : RetainedModel() {
    init {
        Napier.d(tag = TAG) { "init" }
    }

    val progressFactorStateFlow =
        repository
            .getResumePointMsFlow(item.id)
            .map { progressResult ->
                if (item.duration <= 0f) return@map 0f
                if (progressResult == null) return@map 0f

                val (progressMs, isFinished) = progressResult
                if (isFinished) 1f else progressMs.div(item.duration.toFloat()).coerceIn(0f, 1f)
            }.stateInRetainedModel(
                initialValue = 0f,
            )
}
