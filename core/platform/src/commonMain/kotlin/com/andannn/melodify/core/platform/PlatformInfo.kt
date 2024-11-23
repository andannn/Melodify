package com.andannn.melodify.core.platform

internal const val DATABASE_FILE_NAME = "melodify_database.db"

interface PlatformInfo {
    val fileDir: String

    val cacheDir: String

    val databasePath: String
}