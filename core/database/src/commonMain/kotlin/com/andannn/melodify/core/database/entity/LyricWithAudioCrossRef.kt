package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.andannn.melodify.core.database.Tables

internal object LyricWithAudioCrossRefColumns {
    const val MEDIA_STORE_ID = "lyric_with_audio_cross_ref_media_store_id"
    const val LYRIC_ID = "lyric_with_audio_cross_ref_lyric_id"
}

@Entity(
    tableName = Tables.LYRIC_WITH_AUDIO_CROSS_REF,
    primaryKeys = [LyricWithAudioCrossRefColumns.MEDIA_STORE_ID, LyricWithAudioCrossRefColumns.LYRIC_ID],
    foreignKeys = [
        ForeignKey(
            entity = LyricEntity::class,
            parentColumns = [LyricColumns.ID],
            childColumns = [LyricWithAudioCrossRefColumns.LYRIC_ID],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = [LyricWithAudioCrossRefColumns.LYRIC_ID]),
    ],
)
data class LyricWithAudioCrossRef(
    @ColumnInfo(name = LyricWithAudioCrossRefColumns.MEDIA_STORE_ID)
    val mediaStoreId: String,
    @ColumnInfo(name = LyricWithAudioCrossRefColumns.LYRIC_ID)
    val lyricId: Long,
)
