/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Fts4
import androidx.room3.PrimaryKey

@Fts4(
    contentEntity = PlayListEntity::class,
)
@Entity(tableName = "play_list_fts_table")
data class PlayListFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    @ColumnInfo(name = "play_list_name")
    val name: String,
)
