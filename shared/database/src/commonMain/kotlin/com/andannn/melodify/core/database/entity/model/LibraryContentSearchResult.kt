package com.andannn.melodify.core.database.entity.model

import androidx.room.ColumnInfo

data class LibraryContentSearchResult(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "content")
    val content: String,
)
