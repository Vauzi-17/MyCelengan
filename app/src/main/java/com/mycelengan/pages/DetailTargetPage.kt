package com.mycelengan.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mycelengan.AuthViewModel
import com.mycelengan.RupiahIcon
import com.mycelengan.TargetItem
import com.mycelengan.formatRupiah
import com.mycelengan.formatRupiahInput
import com.mycelengan.formatSignedRupiah
import com.mycelengan.parseRupiah
import com.mycelengan.ui.theme.bluelogo
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome
import kotlin.math.absoluteValue


@Composable
fun TargetDetailPage(
    targetId: String,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val ctx = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // realtime target
    val targetData = authViewModel.currentTarget.observeAsState()

    // realtime history
    val history = authViewModel.targetHistory.observeAsState(emptyList())

    // load realtime data
    LaunchedEffect(targetId) {
        authViewModel.subscribeTargetDetail(targetId)
        authViewModel.subscribeTargetHistory(targetId)
    }

    val item = targetData.value
    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val percent =
        if (item.targetAmount == 0) 0f
        else (item.currentAmount.toFloat() / item.targetAmount).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ====================== TOP BAR =========================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = item.title,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.weight(1f))

                Spacer(Modifier.width(8.dp))
            }

            // ====================== SCROLL CONTENT =========================
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 120.dp)
            ) {

            // Header icon
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            resolveTargetIcon(item.icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // ====================== DIALOG =========================
            if (showDialog) {
                EditProgressDialog(
                    onDismiss = { showDialog = false },
                    onSave = { amount, desc, isAdd ->
                        authViewModel.updateTargetProgress(
                            targetId = item.id,
                            amount = amount,
                            isAdd = isAdd,
                            desc = desc
                        ) {
                            Toast.makeText(ctx, "Progress diperbarui", Toast.LENGTH_SHORT).show()
                        }
                        showDialog = false
                    }
                )
            }

            // ====================== DETAIL CARD =========================
            Text(
                "Detail Target",
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(Modifier.padding(20.dp)) {

                    Text(
                        formatRupiah(item.targetAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        item.subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    InfoRow(
                        label = "Progres",
                        value = "${formatRupiah(item.currentAmount)} / ${formatRupiah(item.targetAmount)}"
                    )
                }
            }

            // ====================== PROGRESS =========================
            Text(
                "Progress Tabungan",
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(Modifier.padding(20.dp)) {

                    // Progress percentage text
                    Text(
                        "${(percent * 100).toInt()}%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LinearProgressIndicator(
                        progress = percent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // ====================== HISTORY =========================
                    Text(
                        "Riwayat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                    )

                    if (history.value.isEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Belum ada riwayat",
                                modifier = Modifier.padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        history.value.forEach { log ->
                            val amount = (log["amount"] as? Long)?.toInt() ?: 0
                            val type = log["type"].toString()
                            val desc = log["desc"].toString()
                            val timestamp = log["timestamp"]

                            HistoryRow(amount, type, desc, timestamp)
                        }
                    }
                }
            }
        }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 88.dp),
            containerColor = bluelogo,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            onClick = { showDialog = true }
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Tambah tabungan"
            )
        }
    }
}

@Composable
fun HistoryRow(amount: Int, type: String, desc: String, timestamp: Any?) {
    val color = if (type == "add") colorIncome else colorExpense
    val prefix = if (type == "add") "+" else "-"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(desc, fontWeight = FontWeight.Medium)

                val dateString = timestamp?.toString()?.take(10).orEmpty()

                Text(dateString, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(
                text = formatSignedRupiah(type, amount),
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}




@Composable
fun InfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (valueColor != androidx.compose.ui.graphics.Color.Unspecified)
                valueColor
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EditProgressDialog(
    onDismiss: () -> Unit,
    onSave: (Int, String, Boolean) -> Unit
) {
    var mode by remember { mutableStateOf(0) } // 0=Tambah, 1=Kurangi
    var nominal by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    val cs = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(cs.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    "Catat Progress",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // Switch tambah / kurangi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cs.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (mode == 0) colorIncome else cs.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { mode = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+ Tambah",
                            color = if (mode == 0) cs.onPrimary else colorIncome
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (mode == 1) colorExpense else cs.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { mode = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "- Kurangi",
                            color = if (mode == 1) cs.onPrimary else colorExpense
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ===============================
                // INPUT NOMINAL + FORMAT RUPIAH
                // ===============================
                OutlinedTextField(
                    value = nominal,
                    onValueChange = { input ->
                        nominal = formatRupiahInput(input)
                    },
                    label = { Text("Nominal") },
                    leadingIcon = { RupiahIcon() },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Keterangan") },
                    leadingIcon = { Icon(Icons.Default.Info, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val realNumber = parseRupiah(nominal)
                            if (realNumber > 0) {
                                onSave(realNumber, desc, mode == 0)
                            }
                        }
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}




