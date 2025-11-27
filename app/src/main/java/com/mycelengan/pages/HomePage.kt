package com.mycelengan.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mycelengan.AuthState
import com.mycelengan.AuthViewModel
import com.mycelengan.R
import com.mycelengan.pages.HomeElement.CustomProgressBar
import com.mycelengan.ui.theme.bluelogo
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome
import com.mycelengan.ui.theme.expensePercent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Target", Icons.Default.AccountBalanceWallet),
        NavItem("Pengaturan", Icons.Default.Settings)
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openSheet = false }
        ) {
            when (selectedIndex) {
                0 -> DrawerHome(authViewModel, onSaved = {
                    openSheet = false
                })
                1 -> DrawerTarget()
            }
        }
    }

    Scaffold(
        topBar = {
            when (selectedIndex) {
                0 -> TopAppBar(title = { Text("MyCelengan", fontWeight = FontWeight.Bold) })
                1 -> TopAppBar(title = { Text("Target", fontWeight = FontWeight.Bold) })
                2 -> TopAppBar(title = { Text("Pengaturan", fontWeight = FontWeight.Bold) })
            }
        },
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        icon = { Icon(navItem.icon, contentDescription = navItem.label) },
                        label = { Text(navItem.label) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = bluelogo
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedIndex != 2) {
                FloatingActionButton(
                    onClick = { openSheet = true },
                    containerColor = bluelogo,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Icon(Icons.Default.Add, contentDescription = "add")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            authViewModel = authViewModel,
            navController = navController,
            darkMode = darkMode,
            onDarkModeChange = onDarkModeChange
        )
    }
}


@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val saldo = authViewModel.saldo.observeAsState(0)
    val income = authViewModel.totalIncome.observeAsState(0)
    val expense = authViewModel.totalExpense.observeAsState(0)
    val transactions = authViewModel.transactions.observeAsState(emptyList())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SaldoCardUI(
                saldo = saldo.value,
                income = income.value,
                expense = expense.value
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Transaksi terbaru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(transactions.value) { item ->
            TransactionCard(
                item = TransactionItem(
                    icon = when (item["icon"]) {
                        "fastfood" -> Icons.Default.Fastfood
                        "shopping" -> Icons.Default.ShoppingCart
                        "train" -> Icons.Default.Train
                        "money" -> Icons.Default.AttachMoney
                        "edit" -> Icons.Default.Edit
                        else -> Icons.Default.Edit
                    },
                    title = item["desc"].toString(),
                    date = item["date"].toString(),
                    amount = if (item["type"] == "income")
                        "+Rp${item["amount"]}"
                    else
                        "-Rp${item["amount"]}",
                    amountColor = if (item["type"] == "income") colorIncome else colorExpense
                ),
                onClick = {}
            )
        }


        item {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { authViewModel.signout() }) {
                Text("Sign out")
            }
        }
    }
}



@Composable
fun SaldoCardUI(
    saldo: Int,
    income: Int,
    expense: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tabungan Saat Ini",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "Rp$saldo",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // pemasukan
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorIncome.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, colorIncome.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = colorIncome,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Pemasukan",
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Rp$income",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorIncome,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // pengeluaran
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorExpense.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, colorExpense.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = colorExpense,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Pengeluaran",
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Rp$expense",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorExpense,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TransactionCard(
    item: TransactionItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    item.date,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = item.amount,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = item.amountColor
            )
        }
    }
}


data class TransactionItem(
    val icon: ImageVector,
    val title: String,
    val date: String,
    val amount: String,
    val amountColor: Color
)

val transactionDummyList = listOf(
    TransactionItem(
        icon = Icons.Default.Fastfood,
        title = "Makan Siang",
        date = "12 Des 2023",
        amount = "-Rp 50.000",
        amountColor = colorExpense
    ),
    TransactionItem(
        icon = Icons.Default.AttachMoney,
        title = "Gaji Bulanan",
        date = "1 Des 2023",
        amount = "+Rp 5.000.000",
        amountColor = colorIncome
    ),
    TransactionItem(
        icon = Icons.Default.LocalGroceryStore,
        title = "Belanja Bulanan",
        date = "5 Des 2023",
        amount = "-Rp 350.000",
        amountColor = expensePercent
    )
)


@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    authViewModel: AuthViewModel,
    navController: NavController,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    when (selectedIndex) {
        0 -> HomeContent(modifier = modifier, authViewModel)
        1 -> TargetPage(modifier = modifier, navController)
        2 -> PengaturanPage(modifier = modifier, navController, authViewModel, darkMode = darkMode, onDarkModeChange = onDarkModeChange)
    }
}

fun formatRupiah(input: String): String {
    return input.reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerHome(authViewModel: AuthViewModel,onSaved: () -> Unit) {

    var selectedTab by remember { mutableStateOf(0) } // 0 pengeluaran, 1 pemasukan
    var amount by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("fastfood") }
    val rawAmount = amount.replace(".", "")
    val isFormValid = rawAmount.isNotEmpty() && desc.isNotEmpty() && date.isNotEmpty()
    val context = LocalContext.current



    val icons = listOf(
        "fastfood" to Icons.Default.Fastfood,
        "shopping" to Icons.Default.ShoppingCart,
        "train" to Icons.Default.Train,
        "money" to Icons.Default.AttachMoney,
        "edit" to Icons.Default.Edit
    )


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text(
                "Tambah Transaksi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
        }

        // ========================== TAB SWITCH ==========================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(
                    text = "Pengeluaran",
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selectedTab == 0) colorExpense.copy(alpha = 0.15f)
                            else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 10.dp)
                        .clickable {
                            selectedTab = 0
                        },
                    textAlign = TextAlign.Center,
                    color = if (selectedTab == 0) colorExpense else Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Pemasukan",
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selectedTab == 1) colorIncome.copy(alpha = 0.15f)
                            else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 10.dp)
                        .clickable {
                            selectedTab = 1
                        },
                    textAlign = TextAlign.Center,
                    color = if (selectedTab == 1) colorIncome else Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))
        }

        // ========================== AMOUNT ==========================
        // ========================== AMOUNT ==========================
        item {

            var isError by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->

                    val raw = newValue.replace(".", "")

                    if (raw.isEmpty()) {
                        amount = ""
                        isError = false
                        return@OutlinedTextField
                    }

                    if (raw.all { it.isDigit() }) {
                        isError = false
                        amount = formatRupiah(raw)   // tetap format
                    } else {
                        isError = true
                        Toast.makeText(context, "Hanya angka!", Toast.LENGTH_SHORT).show()
                    }
                },
                label = {
                    Text(
                        if (selectedTab == 0) "Jumlah Pengeluaran"
                        else "Jumlah Pemasukan"
                    )
                },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                isError = isError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }


        // ========================== DESCRIPTION ==========================
        item {
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Deskripsi") },
                leadingIcon = { Icon(Icons.Default.Info, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }

        item {
            var showDatePicker by remember { mutableStateOf(false) }
            val todayMillis = System.currentTimeMillis()
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)

            LaunchedEffect(Unit) {
                if (date.isBlank()) {
                    date = formatDate(todayMillis)
                }
            }

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Tanggal") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            showDatePicker = true
                        }
                    )
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                date = formatDate(millis)
                            }
                            showDatePicker = false
                        }) {
                            Text("Pilih")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Batal")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }


            Spacer(Modifier.height(16.dp))
        }






        // ========================== ICON PICKER ==========================
        item {
            Text("Pilih Icon", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                icons.forEach { (key, vector) ->
                    Icon(
                        imageVector = vector,
                        contentDescription = null,
                        tint = if (selectedIcon == key) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedIcon == key)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                else
                                    Color.Transparent
                            )
                            .padding(8.dp)
                            .clickable { selectedIcon = key }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // ========================== SUBMIT BUTTON ==========================
        item {
            Button(
                onClick = {
                    val rawAmount = amount.replace(".", "").trim()

                    authViewModel.addTransaction(
                        amount = rawAmount.toInt(),   // ✔ aman
                        desc = desc,
                        date = date,
                        type = if (selectedTab == 1) "income" else "expense",
                        iconName = selectedIcon
                    )

                    Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    onSaved() // menutup drawer
                },

                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) bluelogo else Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selectedTab == 0) "Simpan Pengeluaran"
                    else "Simpan Pemasukan"
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}



@Composable
fun DrawerTarget() {

    var targetname by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    // Gallery launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImage = uri }
    )



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),        // naik kalau keyboard muncul
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text(
                "Tambah Target",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))
        }

        // Foto Target
        item {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImage == null) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "placeholder",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Gambar target", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Unggah gambar untuk target tabunganmu", color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(16.dp))

            OutlinedButton(onClick = {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text("Pilih Gambar")
            }

            Spacer(Modifier.height(32.dp))
        }

        // Nama target
        item {
            OutlinedTextField(
                value = targetname,
                onValueChange = { targetname = it },
                label = { Text("Apa yang ingin kamu capai?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }

        // Nominal
        item {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Berapa nominal yang dibutuhkan") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }

        // Tanggal
        item {
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Kapan target tercapai? (contoh: 12 Jan 2024)") },
                leadingIcon = { Icon(Icons.Default.Alarm, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }

        // Catatan
        item {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan tambahan") },
                leadingIcon = { Icon(Icons.Default.Info, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
        }

        // Simpan Button
        item {
            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = bluelogo,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Target")
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}



data class NavItem(
    val label: String,
    val icon: ImageVector
)

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(millis))
}
