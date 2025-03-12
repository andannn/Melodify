package com.andannn.melodify.ui.common.previews

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers.BLUE_DOMINATED_EXAMPLE
import androidx.compose.ui.tooling.preview.Wallpapers.RED_DOMINATED_EXAMPLE

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(
    name = "Red Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    wallpaper = RED_DOMINATED_EXAMPLE
)
@Preview(
    name = "Blue Dark",
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL,
    wallpaper = BLUE_DOMINATED_EXAMPLE
)
@Preview(
    name = "Red Light",
    wallpaper = RED_DOMINATED_EXAMPLE
)
@Preview(
    name = "Blue Light",
    wallpaper = BLUE_DOMINATED_EXAMPLE
)
@Preview(name = "200%", fontScale = 2f)
@Preview(name = "150%", fontScale = 1.5f)
@Preview(name = "85%", fontScale = 0.85f)
annotation class CustomPreviews
