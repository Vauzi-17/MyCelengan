package com.mycelengan.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.AuthState
import com.mycelengan.AuthViewModel
import com.mycelengan.R
import com.mycelengan.ui.theme.bluelogo

@Composable
fun WelcomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val authState = authViewModel.authState.observeAsState()

    // Jika sudah login → skip welcome
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("welcome") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Safety return kalau user authenticated
    if (authState.value is AuthState.Authenticated) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // ----------- TOP BAR CUSTOM (di atas) -----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_out),
                contentDescription = null,
                colorFilter = ColorFilter.tint(bluelogo),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "My Celengan",
                fontWeight = FontWeight.Bold,
                color = bluelogo,
                fontSize = 20.sp
            )
        }

        // ----------- ILLUSTRATION (di tengah) -----------
        Image(
            painter = painterResource(id = R.drawable.babi),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .graphicsLayer { alpha = 0.99f }
                .drawWithContent {
                    val gradient = Brush.radialGradient(
                        colors = listOf(Color.White, Color.Transparent),
                        center = center,
                        radius = size.minDimension / 1.9f
                    )
                    drawContent()
                    drawRect(
                        brush = gradient,
                        blendMode = BlendMode.DstIn
                    )
                }
        )

        // ----------- BOTTOM CONTENT (title, subtitle, buttons) -----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ----------- TITLE -----------
            Text(
                text = "Make Your Dreams Come True, One Saving at a Time",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(Modifier.height(16.dp))

            // ----------- SUBTITLE -----------
            Text(
                text = "Set your goals, track your progress, and achieve your financial targets in a fun and easy way",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // ----------- BUTTON GET STARTED -----------
            Button(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(bluelogo)
            ) {
                Text("Get Started", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            // ----------- BUTTON LOGIN -----------
            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log In", fontWeight = FontWeight.Bold)
            }
        }
    }
}

