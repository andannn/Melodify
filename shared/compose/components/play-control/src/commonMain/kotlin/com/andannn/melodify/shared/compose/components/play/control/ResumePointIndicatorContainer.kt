/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.play.control

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.VideoItemModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.components.play.control.internal.ResumePointIndicator
import io.github.aakira.napier.Napier
import io.github.andannn.RetainedModel
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "ResumePointIndicatorContainer"

@Composable
fun ResumePointIndicatorContainer(modifier: Modifier = Modifier) {
    val currentMedia by retainResumePointIndicatorContainerModel().currentPlayingMediaFlow.collectAsStateWithLifecycle()

    val media = currentMedia
    if (media is VideoItemModel) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            ResumePointIndicator(
                modifier = Modifier.padding(bottom = 100.dp),
                videoId = media.id.toLong(),
            )
        }
    }
}

@Composable
private fun retainResumePointIndicatorContainerModel(repository: Repository = LocalRepository.current) =
    retainRetainedModel(
        repository,
    ) {
        ResumePointIndicatorContainerModel(repository)
    }

private class ResumePointIndicatorContainerModel(
    repository: Repository,
) : RetainedModel() {
    val currentPlayingMediaFlow =
        repository
            .getPlayingMediaStateFlow()
            .stateIn(
                scope = retainedScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    init {
        Napier.d(tag = TAG) { "init ${this.hashCode()}" }

        retainedScope.launch {
            repository.observePlayBackEndEvent().collect { media ->
                if (media is VideoItemModel) {
                    Napier.d(tag = TAG) { "mark video completed id: ${media.id}" }
                    repository.markVideoCompleted(media.id.toLong())
                }
            }
        }
    }
}
