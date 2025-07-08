package com.andannn.melodify.core.database.entity.fts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables.LIBRARY_FTS_MEDIA
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.MediaEntity

@Fts4(
    contentEntity = MediaEntity::class,
)
@Entity(tableName = LIBRARY_FTS_MEDIA)
data class MediaFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    @ColumnInfo(name = MediaColumns.TITLE)
    val title: String,
)
