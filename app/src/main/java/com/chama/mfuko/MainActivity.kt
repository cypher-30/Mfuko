package com.chama.mfuko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.chama.mfuko.ui.features.main.MainViewModel
import com.chama.mfuko.ui.features.splash.MfukoSplash
import com.chama.mfuko.ui.navigation.AppNavHost
import com.chama.mfuko.ui.theme.MfukoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate()/setContent — shows the system
        // splash window (Theme.Mfuko.Splash, instant green background) and
        // auto-dismisses it on the first Compose frame, handing off seamlessly
        // to the MfukoSplash composable below. See APP_REDESIGN_BRIEF.md.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            MfukoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel = hiltViewModel()
                    val startDestination = mainViewModel.startDestination.collectAsState().value
                    val navController = rememberNavController()
                    var splashAnimationDone by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        if (startDestination != null) {
                            AppNavHost(
                                navController = navController,
                                startDestination = startDestination
                            )
                        }
                        if (!splashAnimationDone) {
                            MfukoSplash(
                                contentReady = startDestination != null,
                                onFinished = { splashAnimationDone = true }
                            )
                        }
                    }
                }
            }
        }
    }
}