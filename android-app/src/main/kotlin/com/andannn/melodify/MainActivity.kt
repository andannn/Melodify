/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andannn.melodify.core.syncer.MediaLibrarySyncRepository
import com.andannn.melodify.core.syncer.SyncJobService
import com.andannn.melodify.core.syncer.SyncWorkHelper
import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.UserPreferenceRepository
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.ui.LocalScreenOrientationController
import com.andannn.melodify.ui.LocalSystemUiController
import com.andannn.melodify.ui.app.MelodifyMobileApp
import com.andannn.melodify.ui.player.LocalPlayerStateHolder
import com.andannn.melodify.ui.player.PipPlayer
import com.andannn.melodify.ui.player.PlayerStateHolder
import com.andannn.melodify.util.ConnectFailedAlertDialog
import com.andannn.melodify.util.MediaFileDeleteHelperImpl
import com.andannn.melodify.util.PipParamUpdateEffect
import com.andannn.melodify.util.brightness.AndroidBrightnessController
import com.andannn.melodify.util.brightness.LocalBrightnessController
import com.andannn.melodify.util.immersive.AndroidSystemUiController
import com.andannn.melodify.util.orientation.ScreenOrientationController
import com.andannn.melodify.util.rememberIsInPipMode
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "MainActivity"

private val runTimePermissions =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainActivityViewModel by viewModel()
    private val userPreferenceRepository: UserPreferenceRepository by inject()
    private val mediaFileDeleteHelper: MediaFileDeleteHelper by inject()
    private val syncWorkHelper: SyncWorkHelper by inject()
    private val syncer: MediaLibrarySyncRepository by inject()

    private val deleteHelper: MediaFileDeleteHelperImpl
        get() = mediaFileDeleteHelper as MediaFileDeleteHelperImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Napier.d(tag = TAG) { "onCreate() savedInstanceState $savedInstanceState" }

        SyncJobService.scheduleSyncLibraryJob(this)
        syncWorkHelper.registerPeriodicSyncWork(this)

        val deleteIntentSenderLauncher: ActivityResultLauncher<IntentSenderRequest> =
            registerForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
            ) { result ->
                deleteHelper.onResult(result)
            }
        deleteHelper.intentSenderLauncher = deleteIntentSenderLauncher

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
                    runTimePermissions
                        .filter { permission ->
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                permission,
                            ) == PackageManager.PERMISSION_DENIED
                        }.let {
                            Napier.d(tag = TAG) { "requesting permissions: $it" }
                            launcher.launch(it.toTypedArray())
                        }
                }
            }

            LaunchedEffect(permissionGranted) {
                if (permissionGranted && savedInstanceState == null) {
                    val notSynced = userPreferenceRepository.getLastSuccessfulSyncTime() == null
                    Napier.d(tag = TAG) { "permission granted. notSynced: $notSynced" }
                    if (notSynced) {
                        syncer.startSync()
                    }
                }
            }

            if (uiState is MainUiState.Ready) {
                PipParamUpdateEffect()
            }

            MelodifyTheme {
                val isPipMode = rememberIsInPipMode()

                CompositionLocalProvider(
                    LocalPlayerStateHolder provides retain { PlayerStateHolder() },
                ) {
                    if (isPipMode) {
                        PipPlayer(modifier = Modifier.fillMaxSize())
                    } else {
                        CompositionLocalProvider(
                            LocalScreenOrientationController provides ScreenOrientationController(this),
                            LocalBrightnessController provides AndroidBrightnessController(this),
                            LocalSystemUiController provides AndroidSystemUiController(this),
                        ) {
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteHelper.intentSenderLauncher = null
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
