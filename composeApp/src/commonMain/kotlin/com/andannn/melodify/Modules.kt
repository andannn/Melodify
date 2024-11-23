package com.andannn.melodify

import com.andannn.melodify.core.data.di.dataModule
import com.andannn.melodify.core.syncer.di.syncerModule
import com.andannn.melodify.feature.customtab.di.customTabSettingModule
import com.andannn.melodify.feature.home.di.homeFeatureModule
import com.andannn.melodify.feature.playList.di.playListFeatureModule
import com.andannn.melodify.feature.player.di.playerFeatureModule
import org.koin.core.module.Module

val modules: List<Module> = listOf(
    dataModule,
    syncerModule,

    homeFeatureModule,
    playListFeatureModule,
    playerFeatureModule,
    customTabSettingModule,
)
