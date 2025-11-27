package com.mycelengan.pages

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdfScanner
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycelengan.R
import com.mycelengan.ui.theme.targetCard1
import com.mycelengan.ui.theme.targetCard2

@Composable
fun TargetPage(modifier: Modifier = Modifier, navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Daftar Target",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "2 Target Aktif",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            TargetCard(
                title = "Jalan-Jalan",
                subtitle = "Target Liburan",
                targetValue = "Rp 400.000",
                perMonth = "Rp 100.000 / bulan",
                progress = 0.16f,
                progressText = "16%",
                monthsLeft = "4 Bulan Lagi",
                color = targetCard1,
                icon = Icons.Default.Flight,
                onClick = {
                    navController.navigate("jalan")
                }
            )

            TargetCard(
                title = "HP Baru",
                subtitle = "Smartphone Impian",
                targetValue = "Rp 4.000.000",
                perMonth = "Rp 500.000 / bulan",
                progress = 0.16f,
                progressText = "16%",
                monthsLeft = "8 Bulan Lagi",
                color = targetCard2,
                icon = Icons.Default.Smartphone,
                onClick = {
                    navController.navigate("jalan")
                }
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}


@Composable
fun TargetCard(
    title: String,
    subtitle: String,
    targetValue: String,
    perMonth: String,
    progress: Float,
    progressText: String,
    monthsLeft: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            color.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            targetValue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(perMonth, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(color.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.size(60.dp),
                            color = color,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 6.dp
                        )
                        Text(
                            progressText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Footer
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        monthsLeft,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
