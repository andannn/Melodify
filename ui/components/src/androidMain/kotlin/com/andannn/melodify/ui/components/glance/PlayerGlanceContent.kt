package com.andannn.melodify.ui.components.glance

import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import coil3.Bitmap
import coil3.imageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Scale
import coil3.toBitmap
import com.andannn.melodify.ui.common.theme.DarkColorPalette
import com.andannn.melodify.ui.common.theme.LightColorPalette
import com.andannn.melodify.ui.components.R
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.components.playcontrol.PlayerUiState
import com.andannn.melodify.ui.components.playcontrol.rememberPlayerPresenter

@Composable
fun PlayerGlance() {
    GlanceTheme(
        colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            GlanceTheme.colors
        } else {
            ColorProviders(
                light = LightColorPalette,
                dark = DarkColorPalette
            )
        }
    ) {
        val presenter = rememberPlayerPresenter()
        PlayerGlanceContent(
            state = presenter.present(),
            modifier = GlanceModifier
                .background(GlanceTheme.colors.surface)
                .size(width = 180.dp, height = 60.dp)
                .cornerRadius(16.dp)
        )
    }
}

@Composable
fun PlayerGlanceContent(
    state: PlayerUiState,
    modifier: GlanceModifier = GlanceModifier
) {
    val title = when (state) {
        is PlayerUiState.Active -> state.mediaItem.name
        is PlayerUiState.Inactive -> "Nothing Playing"
    }
    val isPlaying = when (state) {
        is PlayerUiState.Active -> state.isPlaying
        is PlayerUiState.Inactive -> false
    }
    val coverRes = when (state) {
        is PlayerUiState.Active -> state.mediaItem.artWorkUri
        is PlayerUiState.Inactive -> null
    }
    val isFavorite = when (state) {
        is PlayerUiState.Active -> state.isFavorite
        is PlayerUiState.Inactive -> false
    }
    Row(
        modifier = modifier.fillMaxWidth().height(80.dp),
    ) {
        AlbumColver(
            modifier = GlanceModifier
                .width(80.dp).fillMaxHeight(),
            contentUrl = coverRes
        )
        Column(
            modifier = GlanceModifier.fillMaxSize(),
        ) {
            Spacer(modifier = GlanceModifier.defaultWeight())
            Title(
                title = title,
                GlanceModifier.padding(horizontal = 8.dp),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            PlayControlArea(
                modifier = GlanceModifier.padding(horizontal = 8.dp),
                eventSink = state.eventSink,
                isFavorite = isFavorite,
                isPlaying = isPlaying,
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }
}

@Composable
fun Title(
    title: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Text(
        modifier = modifier,
        text = title,
        maxLines = 1,
        style = TextStyle(
            color = GlanceTheme.colors.secondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun PlayControlArea(
    isPlaying: Boolean,
    isFavorite: Boolean,
    modifier: GlanceModifier = GlanceModifier,
    eventSink: (PlayerUiEvent) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlanceIcon(
            resource = R.drawable.skip_previous,
            onClick = {
                eventSink.invoke(PlayerUiEvent.OnPreviousButtonClick)
            }
        )
        GlanceIcon(
            resource = if (isPlaying) R.drawable.pause else R.drawable.play_arrow,
            onClick = {
                eventSink.invoke(PlayerUiEvent.OnPlayButtonClick)
            }
        )
        GlanceIcon(
            resource = R.drawable.skip_next,
            onClick = {
                eventSink.invoke(PlayerUiEvent.OnNextButtonClick)
            }
        )
    }
}

@Composable
fun GlanceIcon(
    modifier: GlanceModifier = GlanceModifier,
    resource: Int,
    onClick: () -> Unit = {},
) {
    Image(
        modifier = modifier.size(36.dp).padding(3.dp).clickable(onClick),
        provider = ImageProvider(resource),
        contentDescription = "",
        colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary)
    )
}

@Composable
fun AlbumColver(
    contentUrl: String?,
    modifier: GlanceModifier = GlanceModifier,
) {
    var bitmap: Bitmap? by remember {
        mutableStateOf(null)
    }
    val context = LocalContext.current
    LaunchedEffect(contentUrl) {
        val request = ImageRequest.Builder(context)
            .data(contentUrl)
            .size(128).scale(Scale.FILL)
            .allowHardware(false)
            .memoryCacheKey("$contentUrl.glance.cover")
            .build()

        bitmap =
            when (val result = context.imageLoader.execute(request)) {
                is SuccessResult -> result.image.toBitmap()
                is ErrorResult -> {
                    Log.d("JQN", "AlbumColver: error ${result.throwable}")
                    null
                }
            }
    }
    if (bitmap != null) {
        Image(
            modifier = modifier,
            provider = ImageProvider(
                bitmap!!
            ),
            contentDescription = "Cover"
        )
    } else {
        Image(
            modifier = modifier.background(GlanceTheme.colors.surfaceVariant).padding(20.dp),
            provider = ImageProvider(R.drawable.album),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
            contentDescription = "Cover"
        )
    }
}