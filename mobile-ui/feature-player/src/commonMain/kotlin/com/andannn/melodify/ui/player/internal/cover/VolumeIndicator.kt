/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.util.volumn.VolumeController
import io.github.andannn.RetainedModel
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun VolumeIndicator(
    modifier: Modifier = Modifier,
    volumeStateModel: VolumeStateModel = retainedVolumeState(),
) {
    val current = volumeStateModel.volumeStateFlow.collectAsStateWithLifecycle()
    val maxVolume = volumeStateModel.maxVolume
    VolumeIndicator(modifier = modifier, volume = current.value, maxVolume = maxVolume)
}

@Composable
internal fun retainedVolumeState(volumeController: VolumeController = getKoin().get()) =
    retainRetainedModel(volumeController) {
        VolumeStateModel(volumeController)
    }

internal class VolumeStateModel(
    volumeController: VolumeController,
) : RetainedModel() {
    val volumeStateFlow =
        volumeController
            .getCurrentVolumeFlow()
            .stateIn(
                retainedScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = volumeController.getCurrentVolume(),
            )
    val maxVolume = volumeController.getMaxVolume()
}

@Composable
internal fun VolumeIndicator(
    modifier: Modifier = Modifier,
    volume: Int,
    maxVolume: Int,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val progress =
            remember(volume, maxVolume) {
                volume.toFloat() / maxVolume.toFloat()
            }
        val icon =
            remember(progress) {
                getVolumeIcon(progress)
            }
        Icon(icon, contentDescription = null)

        Spacer(Modifier.width(4.dp))
        LinearProgressIndicator(
            modifier = Modifier.width(120.dp),
            progress = { progress },
        )

        Spacer(Modifier.width(4.dp))
        Text(
            text = volume.toString(),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun getVolumeIcon(progress: Float): ImageVector {
    if (progress == 0f) return Icons.AutoMirrored.Filled.VolumeMute

    return if (progress < 0.5f) {
        Icons.AutoMirrored.Filled.VolumeDown
    } else {
        Icons.AutoMirrored.Filled.VolumeUp
    }
}
