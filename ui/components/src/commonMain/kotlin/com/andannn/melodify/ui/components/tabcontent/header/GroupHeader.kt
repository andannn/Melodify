package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun GroupHeader(
    state: GroupHeaderState,
    modifier: Modifier = Modifier,
) {
    HeaderInfo(
        modifier = modifier,
        coverArtUri = state.cover,
        title = state.title,
        trackCount = state.trackCount,
    )
}

@Composable
private fun HeaderInfo(
    modifier: Modifier = Modifier,
    coverArtUri: String = "",
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    title: String = "",
    trackCount: Int = 0,
) {
    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(5.dp)
                    .height(IntrinsicSize.Max),
        ) {
            AsyncImage(
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.extraSmall),
                model = coverArtUri,
                placeholder = painterResource(defaultImagePlaceholderRes),
                error = painterResource(defaultImagePlaceholderRes),
                contentDescription = "",
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier,
                    text = "$trackCount tracks",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
