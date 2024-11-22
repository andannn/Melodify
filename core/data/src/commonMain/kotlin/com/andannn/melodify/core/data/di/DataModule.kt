package com.andannn.melodify.core.data.di

import com.andannn.melodify.core.data.repository.LyricRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import com.andannn.melodify.core.data.repository.LyricRepositoryImpl
import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.MediaContentRepositoryImpl
import com.andannn.melodify.core.data.repository.PlayListRepositoryImpl
import com.andannn.melodify.core.data.repository.UserPreferenceRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val commonDataModule = module {
    singleOf(::Repository)
    singleOf(::PlayListRepositoryImpl).bind(PlayListRepository::class)
    singleOf(::LyricRepositoryImpl).bind(LyricRepository::class)
    singleOf(::UserPreferenceRepositoryImpl).bind(UserPreferenceRepository::class)
    singleOf(::MediaContentRepositoryImpl).bind(MediaContentRepository::class)
}

expect val dataModule : List<Module>
