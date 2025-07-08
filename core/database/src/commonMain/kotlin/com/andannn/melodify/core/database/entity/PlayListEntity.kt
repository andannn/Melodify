package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object PlayListColumns {
    const val ID = "play_list_id"
    const val NAME = "play_list_name"
    const val CREATED_DATE = "play_list_created_date"
    const val ARTWORK_URI = "play_list_artwork_uri"
}

@Entity(tableName = Tables.PLAY_LIST)
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PlayListColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = PlayListColumns.CREATED_DATE)
    val createdDate: Long,
    @ColumnInfo(name = PlayListColumns.NAME)
    val name: String,
    @ColumnInfo(name = PlayListColumns.ARTWORK_URI)
    val artworkUri: String?,
)
