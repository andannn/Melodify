package com.andannn.melodify.feature.common.util

import androidx.compose.runtime.Composable
import org.koin.core.scope.Scope

@Composable
expect fun getUiRetainedScope(): Scope?
