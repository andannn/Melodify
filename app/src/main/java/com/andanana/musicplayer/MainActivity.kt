package com.andanana.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.andanana.musicplayer.core.designsystem.theme.MusicPlayerTheme
import com.andanana.musicplayer.ui.MainActivityViewModel
import com.andanana.musicplayer.ui.SimpleMusicApp
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val runTimePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private val mainViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            true
        }
        setContent {
            MusicPlayerTheme {
                var permissionGranted by remember {
                    mutableStateOf(isPermissionGranted())
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = {
                        it.forEach { (permission, granted) ->
                            if (!granted) {
                                finish()
                            }
                        }
                        permissionGranted = true
                    }
                )
                if (!permissionGranted) {
                    SideEffect {
                        runTimePermissions.filter {
                            ContextCompat.checkSelfPermission(
                                /* context = */ this,
                                /* permission = */ it
                            ) == PackageManager.PERMISSION_DENIED
                        }.let {
                            launcher.launch(it.toTypedArray())
                        }
                    }
                }

                if (permissionGranted) {
                    SimpleMusicApp()
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
