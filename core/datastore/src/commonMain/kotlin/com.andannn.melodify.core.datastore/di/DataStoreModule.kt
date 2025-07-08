package com.andannn.melodify.core.datastore.di

import com.andannn.melodify.core.datastore.DATA_STORE_FILENAME
import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.datastore.createDataStore
import com.andannn.melodify.core.platform.PlatformInfo
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

val userPreferencesModule: Module =
    module {
        single<UserSettingPreferences> {
            val datastore =
                createDataStore(
                    producePath = {
                        get<PlatformInfo>()
                            .fileDir
                            .toPath()
                            .resolve(DATA_STORE_FILENAME)
                            .toString()
                    },
                )
            UserSettingPreferences(datastore)
        }
    }
