package com.mycelengan.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.AuthViewModel
import com.mycelengan.ProfileViewModel

@Composable
fun EditUsernameDialog(
    usernameNow: String,
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(usernameNow) }

    val isValid = username.length >= 3

    // Background dim layer
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {

        // CARD POPUP
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
                    text = "Ganti Username",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(20.dp))

                // Username input
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username Baru") },
                    singleLine = true,
                    isError = username.isNotEmpty() && !isValid,
                    modifier = Modifier.fillMaxWidth()
                )

                // Error text
                if (username.isNotEmpty() && !isValid) {
                    Text(
                        text = "Minimal 3 karakter",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
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
                            if (username != usernameNow) {
                                viewModel.updateUsername(username, authViewModel)
                            }
                            onDismiss()
                        },
                        enabled = isValid
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


