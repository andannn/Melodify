package com.andannn.melodify.shared.compose.common.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
actual fun LinerWaveSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    playing: Boolean,
) {
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        track = { sliderState ->
            LinearWavyProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { sliderState.value },
                stroke =
                    Stroke(
                        width =
                            with(LocalDensity.current) {
                                3.dp.toPx()
                            },
                        cap = StrokeCap.Round,
                    ),
                wavelength = 60.dp,
                waveSpeed = 30.dp,
                amplitude = { progress ->
                    if (!playing || progress <= 0.1f || progress >= 0.95f) {
                        0f
                    } else {
                        1f
                    }
                },
            )
        },
    )
}
