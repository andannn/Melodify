package com.andannn.melodify.core.platform

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::PlatformInfoImpl).bind(PlatformInfo::class)
}
