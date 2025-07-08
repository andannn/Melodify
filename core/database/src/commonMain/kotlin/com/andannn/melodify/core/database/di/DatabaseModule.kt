/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.di

import androidx.room.RoomDatabase
import com.andannn.melodify.core.database.MelodifyDataBase
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.setUpDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val databaseBuilder: Module

val databaseModule =
    module {
        includes(
            databaseBuilder,
            module {
                single<MelodifyDataBase> {
                    get<RoomDatabase.Builder<MelodifyDataBase>>()
                        .setUpDatabase()
                        .build()
                }
                single<LyricDao> { get<MelodifyDataBase>().getLyricDao() }
                single<PlayListDao> { get<MelodifyDataBase>().getPlayListDao() }
                single<MediaLibraryDao> { get<MelodifyDataBase>().getMediaLibraryDao() }
                single<UserDataDao> { get<MelodifyDataBase>().getUserDataDao() }
            },
        )
    }
