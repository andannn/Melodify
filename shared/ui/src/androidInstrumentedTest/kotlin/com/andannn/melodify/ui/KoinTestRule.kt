package com.andannn.melodify.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.andannn.melodify.core.data.di.instrumentDataModel
import com.andannn.melodify.core.platform.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTestRule

internal fun createKoinTestRule() =
    KoinTestRule.create {
        val context = ApplicationProvider.getApplicationContext<Context>()
        androidContext(context)
        modules(
            platformModule,
            instrumentDataModel,
        )
    }
