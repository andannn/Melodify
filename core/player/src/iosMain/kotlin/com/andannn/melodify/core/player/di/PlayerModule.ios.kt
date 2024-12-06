package com.andannn.melodify.core.player.di

import com.andannn.melodify.core.player.AVPlayerWrapper
import com.andannn.melodify.core.player.AVPlayerWrapperWrapperImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformPlayerModule: Module = module {
    singleOf(::AVPlayerWrapperWrapperImpl).bind(AVPlayerWrapper::class)
}

