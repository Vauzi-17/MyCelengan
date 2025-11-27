package com.mycelengan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
            var darkMode by rememberSaveable { mutableStateOf(systemDark) }

            MyCelenganTheme(darkTheme = darkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        darkMode = darkMode,
                        onDarkModeChange = { darkMode = it }
                    )
                }
            }
        }

    }
}

