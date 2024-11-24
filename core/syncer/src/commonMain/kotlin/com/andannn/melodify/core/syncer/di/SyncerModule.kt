package com.andannn.melodify.core.syncer.di

import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.core.syncer.MediaLibrarySyncerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val syncerModule: Module
//= module {
//    singleOf(::MediaLibrarySyncerImpl).bind(MediaLibrarySyncer::class)
//}
