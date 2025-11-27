package com.mycelengan.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.AuthViewModel
import com.mycelengan.pages.Pengaturan.SettingItem
import com.mycelengan.pages.Pengaturan.SettingSwitchItem

@Composable
fun PengaturanPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {

    var notificationEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {

        SettingItem(
            icon = Icons.Default.AccountCircle,
            title = "Pengaturan akun",
            subtitle = "Profil, Email",
            onClick = {
                navController.navigate("akun")
            }
        )

        SettingSwitchItem(
            icon = Icons.Default.DarkMode,
            title = "Mode Gelap",
            subtitle = "Aktifkan mode gelap",
            checked = darkMode,
            onCheckedChange = { onDarkModeChange(it) }
        )

        SettingSwitchItem(
            icon = Icons.Default.Notifications,
            title = "Notifikasi",
            subtitle = "Aktifkan push notifikasi",
            checked = notificationEnabled,
            onCheckedChange = { notificationEnabled = it }
        )

        SettingItem(
            icon = Icons.Default.Language,
            title = "Bahasa",
            onClick = {}
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        SettingItem(
            icon = Icons.Default.Info,
            title = "Versi Aplikasi",
            subtitle = "v1.0.0",
            onClick = { }
        )

        SettingItem(
            icon = Icons.Default.QuestionMark,
            title = "Bantuan",
            subtitle = "Pusat bantuan, Hubungi kami",
            onClick = { }
        )

        SettingItem(
            icon = Icons.Default.Star,
            title = "Beri Peringkat Aplikasi",
            onClick = { }
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        SettingItem(
            icon = Icons.Default.CheckCircle,
            title = "Periksa Pembaruan",
            onClick = { }
        )

        SettingItem(
            icon = Icons.Default.Delete,
            title = "Hapus Semua Data",
            onClick = { }
        )
    }
}
