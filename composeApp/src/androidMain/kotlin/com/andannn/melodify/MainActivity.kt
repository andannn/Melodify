/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andannn.melodify.core.data.internal.UserPreferenceRepository
import com.andannn.melodify.core.syncer.SyncJobService
import com.andannn.melodify.core.syncer.SyncWorkHelper
import com.andannn.melodify.ui.theme.MelodifyTheme
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "MainActivity"

private val runTimePermissions =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainActivityViewModel by viewModel()
    private val userPreferenceRepository: UserPreferenceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        SyncJobService.scheduleSyncLibraryJob(this)
        SyncWorkHelper.registerPeriodicSyncWork(this)

        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.dark(
                    scrim = Color.TRANSPARENT,
                ),
        )

        var deleteHelper: MediaFileDeleteHelperImpl? = null
        val deleteIntentSenderLauncher: ActivityResultLauncher<IntentSenderRequest> =
            registerForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
            ) { result ->
                deleteHelper?.onResult(result)
            }

        deleteHelper =
            MediaFileDeleteHelperImpl(
                deleteIntentSenderLauncher,
            )

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
            val coroutineScope = rememberCoroutineScope()
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
                    runTimePermissions
                        .filter {
                            ContextCompat.checkSelfPermission(
                                // context =
                                this@MainActivity,
                                // permission =
                                it,
                            ) == PackageManager.PERMISSION_DENIED
                        }.let {
                            launcher.launch(it.toTypedArray())
                        }
                }
            }

            LaunchedEffect(permissionGranted) {
                if (permissionGranted && savedInstanceState == null) {
                    val notSynced = userPreferenceRepository.getLastSuccessfulSyncTime() == null
                    Napier.d(tag = TAG) { "permission granted. notSynced: $notSynced" }
                    if (notSynced) {
                        SyncWorkHelper.doOneTimeSyncWork(this@MainActivity)
                    }
                }
            }

            CompositionLocalProvider(
                LocalMediaFileDeleteHelper provides deleteHelper,
            ) {
                MelodifyTheme(darkTheme = true, isDynamicColor = true) {
                    when (uiState) {
                        is MainUiState.Error -> {
                            ConnectFailedAlertDialog(
                                onDismiss = { finish() },
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
