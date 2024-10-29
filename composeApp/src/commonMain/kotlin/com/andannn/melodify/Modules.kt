package com.andannn.melodify

import com.andannn.melodify.core.data.di.dataModule
import com.andannn.melodify.core.database.di.databaseModule
import com.andannn.melodify.core.datastore.di.userPreferencesModule
import com.andannn.melodify.core.network.di.serviceModule
import com.andannn.melodify.core.player.di.playerModule
import com.andannn.melodify.feature.customtab.di.customTabSettingModule
import com.andannn.melodify.feature.home.di.homeFeatureModule
import com.andannn.melodify.feature.playList.di.playListFeatureModule
import com.andannn.melodify.feature.player.di.playerFeatureModule
import org.koin.core.module.Module

val modules: List<Module> = listOf(
    *dataModule.toTypedArray(),

    playerModule,
    serviceModule,
    userPreferencesModule,
    databaseModule,

    homeFeatureModule,
    playListFeatureModule,
    playerFeatureModule,
    customTabSettingModule,
)
