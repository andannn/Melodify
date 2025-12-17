/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import platform.Foundation.NSBundle

internal fun getSchemaDirectoryPath(): String = checkNotNull(NSBundle.mainBundle().resourcePath) + "/schemas"
