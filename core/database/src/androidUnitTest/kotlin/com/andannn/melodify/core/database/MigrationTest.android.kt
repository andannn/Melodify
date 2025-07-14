/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper

actual fun getMigrationTestHelper(fileName: String): MigrationTestHelper =
    error(
        "Android Unit test can not do database migration test.",
    )
