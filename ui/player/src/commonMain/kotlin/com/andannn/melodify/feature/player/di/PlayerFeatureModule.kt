package com.andannn.melodify.ui.player.di

import com.andannn.melodify.ui.player.PlayerStateViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val playerFeatureModule = module {
    viewModelOf(::PlayerStateViewModel)
}