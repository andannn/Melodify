package com.andannn.melodify.core.datastore.di

import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.datastore.createDataStore
import com.andannn.melodify.core.datastore.dataStoreFileName
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val userPreferencesModule: Module = module {
    single<UserSettingPreferences> {
        val datastore = createDataStore(
            producePath = {
                File(System.getProperty("user.home") + ".melodify/cache/").resolve(dataStoreFileName)
                    .toString()
            }
        )
        UserSettingPreferences(datastore)
    }
}