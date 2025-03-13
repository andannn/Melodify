package com.andannn.melodify.ui.components.common

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.andannn.melodify.core.data.Repository

val LocalRepository: ProvidableCompositionLocal<Repository> =
    compositionLocalOf { error("no popup controller") }
