package com.andannn.melodify.core.database

import platform.Foundation.NSBundle

internal fun getSchemaDirectoryPath(): String = checkNotNull(NSBundle.mainBundle().resourcePath) + "/schemas"
