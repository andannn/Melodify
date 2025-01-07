package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry

actual fun getMigrationTestHelper(fileName: String) = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    MelodifyDataBase::class.java,
)