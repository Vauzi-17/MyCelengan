package com.mycelengan.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
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

    // ==========================
    //  SETUP PAGER
    // ==========================
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val scope = rememberCoroutineScope()

    var openSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (openSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openSheet = false }
        ) {
            when (pagerState.currentPage) {
                0 -> DrawerHome(authViewModel) { openSheet = false }
                1 -> DrawerTarget(
                    authViewModel = authViewModel,
                    onSaved = { openSheet = false }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            when (pagerState.currentPage) {
                0 -> TopAppBar(title = { Text("MyCelengan", fontWeight = FontWeight.Bold) })
                1 -> TopAppBar(title = { Text("Target", fontWeight = FontWeight.Bold) })
                2 -> TopAppBar(title = { Text("Pengaturan", fontWeight = FontWeight.Bold) })
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index) // ⬅ geser halus
                            }
                        },
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
            if (pagerState.currentPage != 2) {
                FloatingActionButton(
                    onClick = { openSheet = true },
                    containerColor = bluelogo,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Icon(Icons.Default.Add, contentDescription = "add")
                }
            }
        }
    ) { innerPadding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->

            when (page) {
                0 -> HomeContent(
                    modifier = Modifier.fillMaxSize(),
                    authViewModel = authViewModel
                )

                1 -> TargetPage(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    authViewModel = authViewModel
                )

                2 -> PengaturanPage(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    authViewModel = authViewModel,
                    darkMode = darkMode,
                    onDarkModeChange = onDarkModeChange
                )
            }
        }
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
    var selectedTab by remember { mutableStateOf(0) }
    // 0 = semua, 1 = pemasukan, 2 = pengeluaran

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

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val tabs = listOf("Semua", "Pemasukan", "Pengeluaran")

                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }


        // CEK APAKAH TRANSAKSI KOSONG
        if (transactions.value.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(50.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Transaksi masih kosong",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Silahkan tekan tombol + untuk menambah transaksi",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }

            return@LazyColumn   // hentikan agar items() tidak dipanggil
        }

        // FILTER
        val filteredTransactions = when (selectedTab) {
            1 -> transactions.value.filter { it["type"] == "income" }      // pemasukan
            2 -> transactions.value.filter { it["type"] == "expense" }     // pengeluaran
            else -> transactions.value                                      // semua
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = when (selectedTab) {
                            1 -> "Belum ada pemasukan"
                            2 -> "Belum ada pengeluaran"
                            else -> "Belum ada transaksi"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Silahkan tekan tombol + untuk menambahkan",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
            return@LazyColumn
        }


        items(filteredTransactions) { item ->

            val raw = item["amount"].toString()
            val formattedAmount = formatRupiahStr(raw)

            val finalAmount = if (item["type"] == "income")
                "+Rp$formattedAmount"
            else
                "-Rp$formattedAmount"

            // State untuk dialog konfirmasi
            var showDeleteDialog by remember { mutableStateOf(false) }

            // Dialog Konfirmasi Hapus
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Hapus Transaksi") },
                    text = { Text("Apakah Anda yakin ingin menghapus transaksi ini?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val transactionId = item["id"].toString()
                                val amount = item["amount"].toString().toIntOrNull() ?: 0
                                val type = item["type"].toString()

                                authViewModel.deleteTransaction(transactionId, amount, type)
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Hapus", color = colorExpense)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

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
                    amount = finalAmount,
                    amountColor = if (item["type"] == "income") colorIncome else colorExpense
                ),
                onLongClick = {
                    showDeleteDialog = true
                }
            )
        }   
    }
}

fun formatRupiahStr(value: String): String {
    return value
        .replace(".", "")
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, bluelogo.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Rp${formatRupiah(saldo.toString())}",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
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
                            containerColor = MaterialTheme.colorScheme.background .copy(alpha = 0.1f)
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
                                text = "Rp${formatRupiah(income.toString())}",
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
                            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
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
                                text = "Rp${formatRupiah(expense.toString())}",
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionCard(
    item: TransactionItem,
    onLongClick: () -> Unit  // Tambahkan parameter ini
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
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
                Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(item.date, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(
                text = item.amount,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = item.amountColor
            )
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )
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
        1 -> TargetPage(modifier = modifier, navController, authViewModel)
        2 -> PengaturanPage(
            modifier = modifier,
            navController,
            authViewModel,
            darkMode = darkMode,
            onDarkModeChange = onDarkModeChange
        )
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
fun DrawerHome(authViewModel: AuthViewModel, onSaved: () -> Unit) {

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerTarget(authViewModel: AuthViewModel, onSaved: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var perMonth by remember { mutableStateOf("") }
    var createdDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        createdDate = formatDate(System.currentTimeMillis())
    }

    val icons = listOf(
        "flight" to Icons.Default.Flight,
        "phone" to Icons.Default.Smartphone,
        "shopping" to Icons.Default.ShoppingCart,
        "home" to Icons.Default.Home,
        "gift" to Icons.Default.CardGiftcard,
        "savings" to Icons.Default.Savings
    )

    var selectedIcon by remember { mutableStateOf("flight") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Tambah Target", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
        }

        // Icon Picker
        item {
            Text("Pilih Icon", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                icons.forEach { (key, vector) ->
                    Icon(
                        imageVector = vector,
                        contentDescription = null,
                        tint = if (selectedIcon == key)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedIcon == key)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
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

        // Nama target
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Target") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }

        // Subjudul
        item {
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Deskripsi Target (opsional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }

        // Target Amount
        item {
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = { Text("Total Target (Rp)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }

        // Per bulan
        item {
            OutlinedTextField(
                value = perMonth,
                onValueChange = { perMonth = it },
                label = { Text("Nabung per bulan (Rp)") },
                leadingIcon = { Icon(Icons.Default.Savings, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }

        // Tanggal dibuat
        item {
            OutlinedTextField(
                value = createdDate,
                onValueChange = { },
                label = { Text("Tanggal dibuat") },
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }

        // Tombol Simpan
        item {
            Button(
                onClick = {
                    authViewModel.addTarget(
                        title = name,
                        subtitle = subtitle,
                        icon = selectedIcon,
                        targetAmount = targetAmount.toIntOrNull() ?: 0,
                        perMonth = perMonth.toIntOrNull() ?: 0,
                        createdAt = createdDate
                    )
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = bluelogo
                )
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
