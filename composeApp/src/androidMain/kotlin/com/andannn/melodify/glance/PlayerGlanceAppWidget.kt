package com.andannn.melodify.glance

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.glance.PlayerGlance
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.NoOpPopupController
import io.github.aakira.napier.Napier
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "PlayerGlanceAppWidget"

class PlayerGlanceAppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Napier.d(tag = TAG) { "provideGlance called" }
        provideContent {
            CompositionLocalProvider(
                LocalPopupController provides remember { NoOpPopupController() },
                LocalRepository provides remember { getKoin().get<Repository>() }
            ) {
                PlayerGlance()
            }
        }

    }
}
