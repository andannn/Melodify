package com.andannn.melodify.core.database.entity.fts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables.LIBRARY_FTS_ALBUM
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.AlbumEntity

@Fts4(
    contentEntity = AlbumEntity::class
)
@Entity(tableName = LIBRARY_FTS_ALBUM)
data class AlbumFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    @ColumnInfo(name = AlbumColumns.TITLE)
    val title: String,
)