package com.andannn.melodify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.andannn.melodify.navigation.routes.HomePresenterFactory
import com.andannn.melodify.navigation.routes.HomeUiFactory
import com.andannn.melodify.navigation.routes.LibraryContentPresenterFactory
import com.andannn.melodify.navigation.routes.LibraryContentUiFactory
import com.andannn.melodify.navigation.routes.LibraryPresenterFactory
import com.andannn.melodify.navigation.routes.LibraryUiFactory
import com.andannn.melodify.ui.common.widgets.AndroidBackHandler
import com.andannn.melodify.ui.components.common.HomeScreen
import com.andannn.melodify.ui.components.playcontrol.PlayerPresenter
import com.andannn.melodify.ui.components.playcontrol.ui.PlayerAreaView
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.dialog.ActionDialogContainer
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun MelodifyMobileApp(
    modifier: Modifier = Modifier,
    circuit: Circuit = buildCircuitMobile(),
    appState: MelodifyAppState = rememberAppState(),
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        snackbarHost = {
            SnackbarHost(appState.snackBarHostState)
        },
    ) {
        val popupController = LocalPopupController.current
        CircuitCompositionLocals(circuit = circuit) {
            val backStack = rememberSaveableBackStack(HomeScreen)
            val navigator = rememberCircuitNavigator(backStack) {

            }

            AndroidBackHandler(enabled = backStack.size > 1) {
                navigator.pop()
            }

            NavigableCircuitContent(navigator, backStack)
        }

        val playerPresenter = remember {
            PlayerPresenter(getKoin().get(), popupController)
        }
        PlayerAreaView(playerPresenter.present())

        ActionDialogContainer()
    }
}
private fun buildCircuitMobile() = buildCircuit(
    presenterFactory = listOf(
        HomePresenterFactory,
        LibraryPresenterFactory,
        LibraryContentPresenterFactory,
    ),
    uiFactory = listOf(
        HomeUiFactory,
        LibraryUiFactory,
        LibraryContentUiFactory
    )
)
private fun buildCircuit(
    presenterFactory: List<Presenter.Factory> = emptyList(),
    uiFactory: List<Ui.Factory> = emptyList(),
): Circuit {
    return Circuit.Builder()
        .addPresenterFactories(presenterFactory)
        .addUiFactories(uiFactory)
        .build()
}