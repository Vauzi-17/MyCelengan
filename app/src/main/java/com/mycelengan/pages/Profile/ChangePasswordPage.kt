package com.mycelengan.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.ProfileViewModel
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome

@Composable
fun ChangePasswordDialog(
    viewModel: ProfileViewModel,
    onDismiss: () -> Unit
) {
    var newPass by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Validasi minimal 6 karakter
    val isValid = newPass.length >= 6

    // Strength detection
    val strength = remember(newPass) {
        when {
            newPass.length < 6 -> "Weak"
            newPass.any { it.isDigit() } && newPass.any { it.isLetter() } -> "Strong"
            else -> "Medium"
        }
    }

    // Warna strength
    val strengthColor = when (strength) {
        "Weak" -> Color.Red
        "Medium" -> colorExpense
        "Strong" -> colorIncome
        else -> Color.Gray
    }

    // Background dim
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(enabled = true, onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {

        // Card dialog
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "Ganti Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(20.dp))

                // Password Input
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("Password Baru") },
                    singleLine = true,
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
                    },
                    isError = newPass.isNotEmpty() && !isValid,
                    modifier = Modifier.fillMaxWidth()
                )

                // Error Text
                if (newPass.isNotEmpty() && newPass.length < 6) {
                    Text(
                        "Password minimal 6 karakter",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Strength bar
                if (newPass.isNotEmpty()) {
                    Text("Strength: $strength", color = strengthColor)

                    Spacer(Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray.copy(alpha = 0.4f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(
                                    when (strength) {
                                        "Weak" -> 0.33f
                                        "Medium" -> 0.66f
                                        else -> 1f
                                    }
                                )
                                .background(strengthColor)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }

                    Spacer(Modifier.width(10.dp))

                    Button(
                        onClick = {
                            viewModel.changePassword(newPass)
                            onDismiss()
                        },
                        enabled = isValid
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}


