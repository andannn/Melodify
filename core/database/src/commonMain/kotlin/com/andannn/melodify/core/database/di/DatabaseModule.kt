package com.andannn.melodify.core.database.di

import androidx.room.RoomDatabase
import com.andannn.melodify.core.database.LyricDao
import com.andannn.melodify.core.database.MelodifyDataBase
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
        },
        module {
            single<MelodifyDataBase> {
                get<RoomDatabase.Builder<MelodifyDataBase>>()
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .build()
            }
        },
    )
}
