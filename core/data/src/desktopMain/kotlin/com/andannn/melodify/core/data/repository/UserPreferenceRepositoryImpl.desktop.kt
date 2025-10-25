/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.repository

import kotlin.io.path.Path
import kotlin.io.path.isDirectory

actual fun isPathValid(path: String): Boolean = Path(path).isDirectory()
