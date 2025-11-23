/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.util

import androidx.room.RoomDatabase
import com.andannn.melodify.core.database.MelodifyDataBase

internal expect fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase>
