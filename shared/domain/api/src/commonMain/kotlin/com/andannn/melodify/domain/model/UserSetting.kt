/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

data class UserSetting(
    val mediaPreviewMode: MediaPreviewMode,
    val libraryPath: Set<String>,
    val lastSuccessfulSyncTime: Long?,
    val defaultAudioPresetDisplaySetting: PresetDisplaySetting?,
    val defaultVideoPresetDisplaySetting: PresetDisplaySetting?,
)
