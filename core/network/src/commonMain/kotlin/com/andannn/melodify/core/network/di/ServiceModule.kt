package com.andannn.melodify.core.network.di

import com.andannn.melodify.core.network.LrclibService
import com.andannn.melodify.core.network.LrclibServiceImpl
import com.andannn.melodify.core.network.clientBuilder
import org.koin.dsl.module

val serviceModule =
    module {
        single<LrclibService> {
            LrclibServiceImpl(
                clientBuilder(),
            )
        }
    }
