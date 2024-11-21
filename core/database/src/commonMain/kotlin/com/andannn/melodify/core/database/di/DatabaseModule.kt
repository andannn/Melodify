package com.andannn.melodify.core.database.di

import androidx.room.RoomDatabase
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.MIGRATION_1_2
import com.andannn.melodify.core.database.MIGRATION_2_3
import com.andannn.melodify.core.database.MelodifyDataBase
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.addFavoritePlayListCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val databaseBuilder: Module

val databaseModule = module {
    includes(
        databaseBuilder,
        module {
            single<LyricDao> { get<MelodifyDataBase>().getLyricDao() }
            single<PlayListDao> { get<MelodifyDataBase>().getPlayListDao() }
        },
        module {
            single<MelodifyDataBase> {
                get<RoomDatabase.Builder<MelodifyDataBase>>()
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .addCallback(addFavoritePlayListCallback)
                    .build()
            }
        },
    )
}
