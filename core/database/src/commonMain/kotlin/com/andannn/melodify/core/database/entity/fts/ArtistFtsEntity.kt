package com.andannn.melodify.core.database.entity.fts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables.LIBRARY_FTS_ALBUM
import com.andannn.melodify.core.database.Tables.LIBRARY_FTS_ARTIST
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistColumns
import com.andannn.melodify.core.database.entity.ArtistEntity

@Fts4(
    contentEntity = ArtistEntity::class
)
@Entity(tableName = LIBRARY_FTS_ARTIST)
data class ArtistFtsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    @ColumnInfo(name = ArtistColumns.NAME)
    val title: String,
)