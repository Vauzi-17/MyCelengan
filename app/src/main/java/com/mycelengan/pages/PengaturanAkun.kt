package com.mycelengan.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.AuthViewModel
import com.mycelengan.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    val userData by authViewModel.userData.observeAsState()

    val username = userData?.get("username") as? String ?: "Unknown"
    val email = userData?.get("email") as? String ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Akun", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {


            ProfileSection(username = username, email = email)

            Divider(Modifier.padding(vertical = 8.dp))

            SectionHeader("Akun")

            SettingsItem(
                icon = Icons.Default.Person,
                title = "Edit Profil",
                subtitle = "Ubah nama & bio",
                onClick = { showEditDialog = true }
            )

            SettingsItem(
                icon = Icons.Default.Email,
                title = "Email",
                subtitle = email,
                onClick = { /* read only */ }
            )

            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Ubah Password",
                subtitle = "Ganti password akun",
                onClick = { showPasswordDialog = true }
            )

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Keluar")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        authViewModel.signout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) { Text("Ya") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal") }
            }
        )
    }

    if (showEditDialog) {
        EditUsernameDialog(
            usernameNow = username,
            viewModel = profileViewModel,
            authViewModel = authViewModel,   // ⬅ tambahkan ini
            onDismiss = { showEditDialog = false }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            viewModel = profileViewModel,
            onDismiss = { showPasswordDialog = false }
        )
    }


}

@Composable
fun ProfileSection(username: String, email: String) {

    val initial = if (username.isNotEmpty())
        username.first().uppercase()
    else
        "?"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Foto profil
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(email, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
