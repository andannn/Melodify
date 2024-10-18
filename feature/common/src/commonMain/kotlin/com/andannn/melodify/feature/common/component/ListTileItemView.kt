package com.andannn.melodify.feature.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier

@Composable
fun ListTileItemView(
    modifier: Modifier = Modifier,
    swapIconModifier: Modifier? = null,
    albumArtUri: String = "",
    isActive: Boolean = false,
    defaultColor: Color = MaterialTheme.colorScheme.surface,
    title: String = "",
    subTitle: String = "",
    trackNum: Int = 0,
    showTrackNum: Boolean = false,
    onMusicItemClick: () -> Unit = {},
    onOptionButtonClick: () -> Unit = {}
) {
    Napier.d("ListTileItemView $albumArtUri")
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isActive) {
            MaterialTheme.colorScheme.inversePrimary
        } else {
            defaultColor
        },
        onClick = onMusicItemClick,
    ) {
        Row(
            modifier =
            Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier) {
                if (showTrackNum) {
                    Text(
                        modifier =
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.extraSmall,
                            )
                            .align(Alignment.Center)
                            .width(30.dp),
                        text = trackNum.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    AsyncImage(
                        modifier =
                        Modifier
                            .size(50.dp)
                            .clip(MaterialTheme.shapes.extraSmall),
                        model = albumArtUri,
                        contentDescription = "",
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (subTitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (swapIconModifier == null) {
                IconButton(
                    onClick = onOptionButtonClick,
                ) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "menu")
                }
            } else {
                Icon(
                    modifier = Modifier.padding(12.dp).then(swapIconModifier),
                    imageVector = Icons.Filled.Menu, contentDescription = "menu"
                )
            }
        }
    }
}
