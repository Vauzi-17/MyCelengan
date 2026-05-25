package com.mycelengan.pages.HomeElement

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.mycelengan.ui.theme.bluelogo
import androidx.compose.material.icons.filled.Add

data class MainNavItem(
    val label: String,
    val index: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainBottomBar(
    selectedPage: Int,
    onPageChange: (Int) -> Unit,
    onScanTap: () -> Unit,
    onScanLongPress: () -> Unit
) {

    val items = listOf(
        MainNavItem("Home", 0),
        MainNavItem("Transaksi", 1),
        MainNavItem("Target", 2),
        MainNavItem("Pengaturan", 3)
    )

    NavigationBar(
        containerColor =
            MaterialTheme.colorScheme.background
    ) {

        items.forEachIndexed { position, item ->

            if (position == 2) {

                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape
                            )
                            .background(
                                bluelogo,
                                CircleShape
                            )
                            .combinedClickable(
                                onClick = onScanTap,
                                onLongClick = onScanLongPress
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Add,
                            contentDescription =
                                "Scan",
                            tint =
                                MaterialTheme.colorScheme
                                    .onPrimary
                        )
                    }
                }
            }

            NavigationBarItem(

                selected =
                    selectedPage ==
                            item.index,

                onClick = {
                    onPageChange(
                        item.index
                    )
                },

                icon = {

                    val icon =
                        when (
                            item.label
                        ) {

                            "Home" ->
                                Icons.Default.Home

                            "Transaksi" ->
                                Icons.AutoMirrored.Filled.ReceiptLong

                            "Target" ->
                                Icons.Default.AccountBalanceWallet

                            else ->
                                Icons.Default.Settings
                        }

                    Icon(
                        icon,
                        null
                    )
                },

                label = {
                    Text(
                        item.label
                    )
                },

                colors =
                    NavigationBarItemDefaults.colors(
                        indicatorColor =
                            bluelogo
                    )
            )
        }
    }
}