/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.andannn.melodify.navigation.HomePresenterFactory
import com.andannn.melodify.navigation.HomeUiFactory
import com.andannn.melodify.navigation.LibraryContentPresenterFactory
import com.andannn.melodify.navigation.LibraryContentUiFactory
import com.andannn.melodify.navigation.LibraryPresenterFactory
import com.andannn.melodify.navigation.LibraryUiFactory
import com.andannn.melodify.navigation.SearchPresenterFactory
import com.andannn.melodify.navigation.SearchUiFactory
import com.andannn.melodify.navigation.TabManagePresenterFactory
import com.andannn.melodify.navigation.TabManageUiFactory
import com.andannn.melodify.ui.common.util.LocalRepository
import com.andannn.melodify.ui.common.widgets.AndroidBackHandler
import com.andannn.melodify.ui.components.common.HomeScreen
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import com.slack.circuit.backstack.BackStack.Record
import com.slack.circuit.backstack.BackStackRecordLocalProvider
import com.slack.circuit.backstack.ProvidedValues
import com.slack.circuit.backstack.providedValuesForBackStack
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "MelodifyMobileApp"

@Composable
fun MelodifyMobileApp(
    modifier: Modifier = Modifier,
    circuit: Circuit = buildCircuitMobile(),
) {
    CompositionLocalProvider(LocalRepository provides remember { getKoin().get() }) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background,
        ) {
            CircuitCompositionLocals(circuit = circuit) {
                val backStack = rememberSaveableBackStack(HomeScreen)
                val navigator =
                    rememberCircuitNavigator(backStack) {
                    }

                AndroidBackHandler(enabled = backStack.size > 1) {
                    navigator.pop()
                }

                NavigableCircuitContent(
                    navigator = navigator,
                    backStack = backStack,
                    providedValues =
                        providedValuesForBackStack(
                            backStack,
                            stackLocalProviders =
                                persistentListOf(
                                    PopupControllerRecordLocalProvider,
                                ),
                        ),
                )
            }
        }
    }
}

object PopupControllerRecordLocalProvider : BackStackRecordLocalProvider<Record> {
    @Composable
    override fun providedValuesFor(record: Record): ProvidedValues =
        object : ProvidedValues {
            @Composable
            override fun provideValues(): ImmutableList<ProvidedValue<*>> =
                persistentListOf(
                    LocalPopupController provides remember { PopupControllerImpl() },
                )
        }
}

private fun buildCircuitMobile() =
    buildCircuit(
        presenterFactory =
            listOf(
                HomePresenterFactory,
                LibraryPresenterFactory,
                LibraryContentPresenterFactory,
                SearchPresenterFactory,
                TabManagePresenterFactory,
            ),
        uiFactory =
            listOf(
                HomeUiFactory,
                LibraryUiFactory,
                LibraryContentUiFactory,
                SearchUiFactory,
                TabManageUiFactory,
            ),
    )

internal fun buildCircuit(
    presenterFactory: List<Presenter.Factory> = emptyList(),
    uiFactory: List<Ui.Factory> = emptyList(),
): Circuit =
    Circuit
        .Builder()
        .addPresenterFactories(presenterFactory)
        .addUiFactories(uiFactory)
        .build()
