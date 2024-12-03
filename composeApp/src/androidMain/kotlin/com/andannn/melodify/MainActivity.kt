package com.andannn.melodify

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andannn.melodify.ui.common.dialog.ConnectFailedAlertDialog
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import android.graphics.Color
import com.andannn.melodify.core.syncer.MediaLibrarySyncer
import com.andannn.melodify.core.syncer.SyncJobService
import com.andannn.melodify.ui.components.menu.MenuController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback

private const val TAG = "MainActivity"

private val runTimePermissions =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope: Scope by activityRetainedScope()

    private val mainViewModel: MainActivityViewModel by viewModel {
        parametersOf(scope.get<MenuController>())
    }

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        SyncJobService.scheduleSyncLibraryJob(this)

        // initialize koin activity retained scope.
        checkNotNull(scope)

        scope.registerCallback(object : ScopeCallback {
            override fun onScopeClose(scope: Scope) {
                scope.get<MenuController>().close()
            }
        })

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT,
            ),
        )

        intentSenderLauncher = registerForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            Napier.d(tag = TAG) { "activity result: $result" }
        }

        lifecycleScope.launch {
            val deleteMediaItemEventFlow = scope.get<MenuController>().deleteMediaItemEventFlow
            deleteMediaItemEventFlow.collect { uris ->
                Napier.d(tag = TAG) { "Requesting delete media items: $uris" }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val editPendingIntent = MediaStore.createTrashRequest(
                        /* resolver = */ contentResolver,
                        /* uris = */ uris.map { Uri.parse(it) },
                        /* value = */ true,
                    )
                    val request =
                        IntentSenderRequest.Builder(editPendingIntent.intentSender).build()

                    intentSenderLauncher.launch(request)
                }
            }
        }

        var uiState by mutableStateOf<MainUiState>(MainUiState.Init)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state
                    .onEach { uiState = it }
                    .collect {}
            }
        }


        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
        // the UI.
        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainUiState.Init -> true
                else -> false
            }
        }

        setContent {
            var permissionGranted by remember {
                mutableStateOf(isPermissionGranted())
            }
            val launcher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = {
                        it.forEach { (_, granted) ->
                            if (!granted) {
                                finish()
                            }
                        }
                        permissionGranted = true
                    },
                )

            if (!permissionGranted) {
                LaunchedEffect(Unit) {
                    runTimePermissions.filter {
                        ContextCompat.checkSelfPermission(
                            /* context = */ this@MainActivity,
                            /* permission = */ it,
                        ) == PackageManager.PERMISSION_DENIED
                    }.let {
                        launcher.launch(it.toTypedArray())
                    }
                }
            }

            LaunchedEffect(permissionGranted) {
                if (permissionGranted && savedInstanceState == null) {
                    Napier.d(tag = TAG) { "scheduling first sync job" }
                    getKoin().get<MediaLibrarySyncer>().syncMediaLibrary()
                }
            }

            MelodifyTheme(darkTheme = true, isDynamicColor = true) {
                when (uiState) {
                    is MainUiState.Error -> {
                        ConnectFailedAlertDialog(
                            onDismiss = { finish() }
                        )
                    }

                    MainUiState.Ready -> {
                        if (permissionGranted) {
                            MelodifyMobileApp()
                        }
                    }

                    MainUiState.Init -> {}
                }
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        runTimePermissions.forEach { permission ->
            when (ContextCompat.checkSelfPermission(this, permission)) {
                PackageManager.PERMISSION_DENIED -> return false
            }
        }
        return true
    }
}
