package com.andannn.melodify.core.platform

internal const val DATABASE_FILE_NAME = "melodify_database.db"

sealed interface Platform

data object Desktop : Platform

interface Mobile : Platform {
    data object Android : Mobile

    data object IOS : Mobile
}

interface PlatformInfo {
    val fileDir: String

    val cacheDir: String

    val databasePath: String

    val platform: Platform
}
