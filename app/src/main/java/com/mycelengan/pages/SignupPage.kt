package com.mycelengan.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.AuthState
import com.mycelengan.AuthViewModel
import com.mycelengan.R

@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Listener state register
    LaunchedEffect(authState.value) {
        when (authState.value) {

            is AuthState.Authenticated -> {
                authViewModel.loadUserData()

                navController.navigate("home") {
                    popUpTo("signup") { inclusive = true }
                    launchSingleTop = true
                }
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState.value as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> Unit
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // LOGO
        item {
            Icon(
                painterResource(R.drawable.logo_out),
                contentDescription = "logo",
                modifier = Modifier.size(160.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Daftar ke My Celengan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(28.dp))
        }

        // FIELD USERNAME
        item {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
                singleLine = true,
                label = { Text("Nama pengguna") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }

        // FIELD EMAIL
        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                leadingIcon = { Icon(Icons.Default.MailOutline, null) },
                singleLine = true,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }

        // FIELD PASSWORD
        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                label = { Text("Kata sandi") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (isPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
        }

        // TOMBOL DAFTAR + LOADING
        item {

            val isLoading = authState.value == AuthState.Loading

            Button(
                onClick = {
                    authViewModel.signup(email, password, username)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Buat akun")
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // PEMBATAS
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Atau",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        // TOMBOL GOOGLE
        item {
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = R.drawable.icons8_google),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Daftar dengan Google", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // TOMBOL APPLE
        item {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(id = R.drawable.icons8_apple_48),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Daftar dengan Apple", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // LINK KE LOGIN
        item {
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Sudah punya akun? Masuk")
            }

            Spacer(Modifier.height(70.dp))
        }
    }
}

