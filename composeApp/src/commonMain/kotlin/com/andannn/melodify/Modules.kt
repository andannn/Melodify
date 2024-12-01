package com.andannn.melodify

import com.andannn.melodify.core.data.di.dataModule
import com.andannn.melodify.core.platform.platformModule
import com.andannn.melodify.core.syncer.di.syncerModule
import com.andannn.melodify.feature.player.di.playerFeatureModule
import org.koin.core.module.Module

expect val uiScopedModule: Module

val modules: List<Module> = listOf(
    uiScopedModule,

    dataModule,
    syncerModule,
    platformModule,

    playerFeatureModule,
)
