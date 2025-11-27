package com.mycelengan.pages

import android.graphics.Color
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mycelengan.R
import com.mycelengan.ui.theme.bluelogo
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome


@Composable
fun jalan(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        var showEditDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Jalan - Jalan",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(Modifier.weight(1f))

            // ======= BUTTON PENCIL DI KANAN =======
            IconButton(
                onClick = { showEditDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }


        // ===========================
        // KONTEN UTAMA (scroll)
        // ===========================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {

            // HEADER CARD ICON
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Flight,
                            contentDescription = null,
                            tint = bluelogo,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            if (showEditDialog) {
                EditTabunganDialog(
                    onDismiss = { showEditDialog = false },
                    onSave = { nominal, desc ->
                        // lakukan sesuatu saat simpan
                        showEditDialog = false
                    }
                )
            }


            // TITLE
            Text(
                text = "Detail Target",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
            )

            // TARGET INFO CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Rp400.000",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = bluelogo
                    )

                    Text(
                        text = "Rp100.000 per bulan",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    InfoRow(label = "Tanggal Dibuat", value = "20 Nov 2025")
                    Spacer(Modifier.height(12.dp))
                    InfoRow(label = "Estimasi", value = "4 Bulan Lagi", valueColor = bluelogo)
                }
            }

            // PROGRESS TITLE
            Text(
                text = "Progress Tabungan",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
            )

            // PROGRESS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // PROGRESS TOP INFO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Terkumpul",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Rp0",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorIncome,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Kekurangan",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Rp400.000",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorExpense,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // PROGRESS BAR
                    LinearProgressIndicator(
                        progress = 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // EMPTY LOG
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )

                        Text(
                            "Tidak ada riwayat",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
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
fun EditTabunganDialog(
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    val context = LocalContext.current

    var mode by remember { mutableStateOf(0) } // 0 tambah, 1 kurangi
    var nominal by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    val cs = MaterialTheme.colorScheme  // shortcut

    Dialog(onDismissRequest = onDismiss) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface)
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                // TITLE
                Text(
                    "Catat Tabungan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = cs.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))


                // SWITCH TAMBAH / KURANGI
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cs.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    // Tambah
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (mode == 0) colorIncome else cs.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
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

                    Spacer(modifier = Modifier.width(4.dp))

                    // Kurangi
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (mode == 1) colorExpense else cs.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { mode = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "− Kurangi",
                            color = if (mode == 1) cs.onPrimary else colorExpense
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))


                // INPUT NOMINAL
                OutlinedTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = cs.primary) },
                    label = { Text("Nominal") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(16.dp))


                // INPUT KETERANGAN
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    leadingIcon = { Icon(Icons.Default.Info, null, tint = cs.onSurfaceVariant) },
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))


                // AKSI BOTTOM (Batal + Simpan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = cs.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            if (nominal.isNotBlank()) {
                                onSave(nominal.toInt(), desc)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Nominal tidak boleh kosong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cs.primary,
                            contentColor = cs.onPrimary
                        )
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}


