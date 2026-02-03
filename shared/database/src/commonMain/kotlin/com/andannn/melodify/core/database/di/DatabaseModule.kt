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
import com.andannn.melodify.core.database.dao.internal.MediaEntityRawQueryDao
import com.andannn.melodify.core.database.dao.internal.PlayListRawQueryDao
import com.andannn.melodify.core.database.dao.internal.SyncerDao
import com.andannn.melodify.core.database.dao.internal.VideoEntityRawQueryDao
import com.andannn.melodify.core.database.helper.paging.MediaPagingProvider
import com.andannn.melodify.core.database.helper.paging.PlayListPagingProvider
import com.andannn.melodify.core.database.helper.paging.VideoPagingProvider
import com.andannn.melodify.core.database.helper.paging.Where
import com.andannn.melodify.core.database.helper.sync.MediaLibrarySyncHelper
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
                daoModule()
            },
        )
    }

private fun Module.daoModule() {
    single<LyricDao> { get<MelodifyDataBase>().getLyricDao() }
    single<PlayListDao> { get<MelodifyDataBase>().getPlayListDao() }
    single<MediaLibraryDao> { get<MelodifyDataBase>().getMediaLibraryDao() }
    single<UserDataDao> { get<MelodifyDataBase>().getUserDataDao() }
    single<MediaEntityRawQueryDao> { get<MelodifyDataBase>().getMediaEntityRawQueryDao() }
    single<VideoEntityRawQueryDao> { get<MelodifyDataBase>().getVideoFlowPagingSource() }
    single<SyncerDao> { get<MelodifyDataBase>().getSyncerDao() }
    single<PlayListRawQueryDao> { get<MelodifyDataBase>().getPlayListRawQueryDao() }
    single<MediaLibrarySyncHelper> { MediaLibrarySyncHelper(get(), get(), get()) }
    factory<MediaPagingProvider> { (wheres: List<Where>) -> MediaPagingProvider(get(), wheres) }
    factory<VideoPagingProvider> { (wheres: List<Where>) -> VideoPagingProvider(get(), wheres) }
    factory<PlayListPagingProvider> { (playListId: Long) ->
        PlayListPagingProvider(
            playListId = playListId,
            get(),
        )
    }
}
