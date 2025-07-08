package com.andannn.melodify.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal class PlatformInfoImpl : PlatformInfo {
    override val fileDir: String
        get() = documentDirectory() + "/file"

    override val cacheDir: String
        get() = documentDirectory() + "/cache"

    override val databasePath: String
        get() = documentDirectory() + '/' + DATABASE_FILE_NAME

    override val platform: Platform
        get() = Mobile.IOS
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    return requireNotNull(documentDirectory?.path)
}
