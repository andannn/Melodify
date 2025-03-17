package com.andannn.melodify.core.data.model

data class UserSetting(
    val mediaPreviewMode: MediaPreviewMode,
    val libraryPath: Set<String>,
    val lastSuccessfulSyncTime: Long?,
)
