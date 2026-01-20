/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.service.siren.di

import com.andannn.melodify.core.network.service.siren.MonsterSirenService
import com.andannn.melodify.core.network.service.siren.MonsterSirenServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val monsterSirenModule =
    module {
        singleOf(::MonsterSirenServiceImpl).bind(MonsterSirenService::class)
    }
