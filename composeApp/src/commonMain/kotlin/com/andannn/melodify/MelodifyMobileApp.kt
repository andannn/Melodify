package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andannn.melodify.navigation.routes.HomePresenterFactory
import com.andannn.melodify.navigation.routes.HomeScreen
import com.andannn.melodify.navigation.routes.HomeUiFactory
import com.andannn.melodify.navigation.routes.LibraryPresenterFactory
import com.andannn.melodify.navigation.routes.LibraryUiFactory
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui

@Composable
fun MelodifyMobileApp(
    modifier: Modifier = Modifier,
    circuit: Circuit = buildCircuitMobile(),
    appState: MelodifyAppState = rememberAppState(),
) {
    CircuitCompositionLocals(circuit = circuit) {
        val backStack = rememberSaveableBackStack(HomeScreen)
        val navigator = rememberCircuitNavigator(backStack) {

        }
        NavigableCircuitContent(navigator, backStack)
    }
//    Scaffold(
//        modifier = modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.surface),
//        snackbarHost = {
//            SnackbarHost(appState.snackBarHostState)
//        },
//    ) {
//        val navController = appState.navController
//        MelodifyNavHost(
//            navHostController = navController,
//            modifier = Modifier.fillMaxWidth(),
//        )
//
//        PlayerAreaView()
//
//        ActionDialogContainer()
//    }
}
private fun buildCircuitMobile() = buildCircuit(
    presenterFactory = listOf(
        HomePresenterFactory,
        LibraryPresenterFactory,
    ),
    uiFactory = listOf(
        HomeUiFactory,
        LibraryUiFactory
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