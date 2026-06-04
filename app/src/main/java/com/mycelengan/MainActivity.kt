package com.mycelengan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.mycelengan.ui.theme.MyCelenganTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashscreen = installSplashScreen()
        var keepSplashScreen = true

        super.onCreate(savedInstanceState)

        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(600)
            keepSplashScreen = false
        }

        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels()

        setContent {

            val systemDark = isSystemInDarkTheme()
            val savedDarkMode by AppSettings
                .darkMode(this@MainActivity)
                .collectAsState(initial = null)
            val notificationEnabled by AppSettings
                .targetReminderNotification(this@MainActivity)
                .collectAsState(initial = false)
            val darkMode = savedDarkMode ?: systemDark

            SideEffect {
                val transparent = android.graphics.Color.TRANSPARENT
                val systemBarStyle =
                    if (darkMode) {
                        SystemBarStyle.dark(transparent)
                    } else {
                        SystemBarStyle.light(transparent, transparent)
                    }

                enableEdgeToEdge(
                    statusBarStyle = systemBarStyle,
                    navigationBarStyle = systemBarStyle
                )
            }

            LaunchedEffect(notificationEnabled) {
                if (notificationEnabled) {
                    TargetReminderScheduler.scheduleDaily(this@MainActivity)
                } else {
                    TargetReminderScheduler.cancel(this@MainActivity)
                }
            }

            MyCelenganTheme(darkTheme = darkMode) {
                MyAppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    authViewModel = authViewModel,
                    darkMode = darkMode,
                    onDarkModeChange = { enabled ->
                        lifecycleScope.launch {
                            AppSettings.setDarkMode(this@MainActivity, enabled)
                        }
                    },
                    notificationEnabled = notificationEnabled,
                    onNotificationChange = { enabled ->
                        lifecycleScope.launch {
                            AppSettings.setTargetReminderNotification(this@MainActivity, enabled)
                        }
                    }
                )
            }
        }

    }
}
