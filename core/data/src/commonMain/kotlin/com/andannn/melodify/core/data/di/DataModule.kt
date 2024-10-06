package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.LyricRepository
import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.core.data.repository.LyricRepositoryImpl
import com.andannn.melodify.core.data.repository.UserPreferenceRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val commonDataModule = module {
    singleOf(::LyricRepositoryImpl).bind(LyricRepository::class)
    singleOf(::UserPreferenceRepositoryImpl).bind(UserPreferenceRepository::class)
}

expect val dataModule : List<Module>
