package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun PlayerHeader(
    modifier: Modifier,
    showTimerIcon: Boolean = true,
    onShrinkButtonClick: () -> Unit,
    onOptionIconClick: () -> Unit,
    onTimerIconClick: () -> Unit,
) {
    Row(modifier = modifier) {
        IconButton(
            modifier =
            Modifier
                .padding(start = 4.dp)
                .rotate(-90f),
            onClick = onShrinkButtonClick,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                contentDescription = "Shrink",
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        if (showTimerIcon) {
            IconButton(
                modifier = Modifier,
                onClick = onTimerIconClick,
            ) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Timer"
                )
            }
        }
        IconButton(
            modifier = Modifier,
            onClick = onOptionIconClick,
        ) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu")
        }
    }
}