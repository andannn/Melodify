package com.andannn.melodify.core.data.repository

import kotlin.io.path.Path
import kotlin.io.path.isDirectory

actual fun isPathValid(path: String): Boolean {
    return Path(path).isDirectory()
}