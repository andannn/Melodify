/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.platform

import java.io.File

class PlatformInfoImpl : PlatformInfo {
    override val fileDir: String
        get() = System.getProperty("user.home") + "/.melodify/file/"

    override val cacheDir: String
        get() = System.getProperty("user.home") + "/.melodify/cache/"

    override val databasePath: String
        get() =
            File(System.getProperty("user.home") + "/.melodify/database/")
                .resolve(
                    DATABASE_FILE_NAME,
                ).toString()
    override val platform: Platform
        get() = Desktop
}
