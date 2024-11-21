package com.andannn.melodify.core.library.mediastore.di

import com.andannn.melodify.core.library.mediastore.MediaLibrary
import com.andannn.melodify.core.library.mediastore.MediaLibraryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val mediaLibraryModule: Module = module {
    singleOf(::MediaLibraryImpl).bind(MediaLibrary::class)
}
