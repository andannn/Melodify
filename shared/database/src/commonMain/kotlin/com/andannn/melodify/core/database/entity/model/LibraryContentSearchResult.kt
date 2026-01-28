/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.entity.model

import androidx.room.ColumnInfo

data class LibraryContentSearchResult(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String,
    /** contentType of [com.andannn.melodify.core.database.dao.MediaType] */
    @ColumnInfo(name = "type")
    val contentType: Int,
)
