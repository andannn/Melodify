package com.andannn.melodify.core.platform

import android.content.Context

class PlatformInfoImpl(
    private val context: Context,
) : PlatformInfo {
    override val fileDir: String
        get() = context.filesDir.toString()
    override val cacheDir: String
        get() = context.cacheDir.toString()
    override val databasePath: String
        get() = context.getDatabasePath(DATABASE_FILE_NAME).absolutePath
    override val platform: Platform
        get() = Mobile.Android
}
