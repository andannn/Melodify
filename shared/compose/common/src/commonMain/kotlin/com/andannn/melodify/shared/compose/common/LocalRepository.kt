/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common

import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.andannn.melodify.domain.Repository
import org.koin.mp.KoinPlatform.getKoin

val LocalRepository: CompositionLocal<Repository> =
    staticCompositionLocalOf {
        getKoin().get()
    }
