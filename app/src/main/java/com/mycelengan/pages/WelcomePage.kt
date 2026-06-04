package com.mycelengan.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.AuthState
import com.mycelengan.AuthViewModel
import com.mycelengan.R

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

    if (authState.value is AuthState.Authenticated) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ----------- HEADER LOGO -----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_out),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "My Celengan",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // ----------- ILUSTRASI -----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.babi),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 280.dp)
                        .padding(horizontal = 40.dp)
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
            }

            Spacer(Modifier.height(24.dp))

            // ----------- KONTEN BAWAH -----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ----------- JUDUL -----------
                Text(
                    text = "Wujudkan Impianmu, Satu Tabungan Setiap Hari",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(Modifier.height(12.dp))

                // ----------- SUBJUDUL -----------
                Text(
                    text = "Buat target, pantau progres, dan capai tujuan finansialmu dengan cara yang mudah.",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(24.dp))

                // ----------- TOMBOL MULAI -----------
                Button(
                    onClick = { navController.navigate("signup") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Mulai Sekarang", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                // ----------- TOMBOL MASUK -----------
                OutlinedButton(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Masuk", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
