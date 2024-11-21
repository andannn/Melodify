package com.andannn.melodify.core.player.di

import com.andannn.melodify.core.player.MediaBrowserManager
import com.andannn.melodify.core.player.MediaBrowserManagerImpl
import com.andannn.melodify.core.player.PlayerWrapper
import com.andannn.melodify.core.player.PlayerWrapperImpl
import com.andannn.melodify.core.player.SleepTimerController
import com.andannn.melodify.core.player.SleepTimerControllerImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val playerModule = module {
    singleOf(::PlayerWrapperImpl).bind(PlayerWrapper::class)
    singleOf(::MediaBrowserManagerImpl).bind(MediaBrowserManager::class)
    singleOf(::SleepTimerControllerImpl).bind(SleepTimerController::class)
}