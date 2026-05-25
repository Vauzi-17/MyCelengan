package com.mycelengan.pages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mycelengan.AuthState
import com.mycelengan.AuthViewModel
import com.mycelengan.ReceiptParser
import com.mycelengan.TransactionDraft
import com.mycelengan.VoiceTransactionParser
import com.mycelengan.pages.HomeElement.FinanceChart
import com.mycelengan.pages.HomeElement.MainBottomBar
import com.mycelengan.transactionCategories
import com.mycelengan.transactionCategoryIcon
import com.mycelengan.ui.theme.bluelogo
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome
import kotlinx.coroutines.launch
import java.io.File
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
val authState =
    authViewModel
        .authState
        .observeAsState()

LaunchedEffect(
    authState.value
) {

    if (
        authState.value
                is AuthState
        .Unauthenticated
    ) {
        navController.navigate(
            "login"
        )
    }
}

val pagerState =
    rememberPagerState(
        initialPage = 0
    ) {
        4
    }

val scope =
    rememberCoroutineScope()

var openSheet by remember {
    mutableStateOf(false)
}

var drawerMode by remember {
    mutableIntStateOf(0)
}

val sheetState =
    rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

if (
    openSheet
) {

    ModalBottomSheet(

        sheetState =
            sheetState,

        onDismissRequest = {
            openSheet = false
        }

    ) {

        DrawerHome(

            authViewModel =
                authViewModel,

            initialMode =
                drawerMode

        ) {

            openSheet = false
        }
    }
}

        Scaffold(

            topBar = {

                val title =
                    when (
                        pagerState.currentPage
                    ) {

                        0 -> "MyCelengan"

                        1 -> "Transaksi"

                        2 -> "Target"

                        else ->
                            "Pengaturan"
                    }

                TopAppBar(
                    title = {
                        Text(
                            title
                        )
                    }
                )
            },

            bottomBar = {

                MainBottomBar(

                    selectedPage =
                        pagerState.currentPage,

                    onPageChange = {

                        scope.launch {

                            pagerState
                                .animateScrollToPage(
                                    it
                                )
                        }
                    },

                    onScanTap = {

                        drawerMode = 1
                        openSheet = true
                    },

                    onScanLongPress = {

                        drawerMode = 3
                        openSheet = true
                    }
                )
            }

        ) { padding ->

            HorizontalPager(
                state = pagerState,
                modifier =
                    modifier
                        .padding(
                            padding
                        )
            ) { page ->

                when (
                    page
                ) {

                    0 -> {

                        HomeContent(
                            modifier =
                                Modifier.fillMaxSize(),
                            authViewModel
                        )
                    }

                    1 -> {

                        TransaksiPage(
                            Modifier.fillMaxSize()
                        )
                    }

                    2 -> {

                        TargetPage(
                            Modifier.fillMaxSize(),
                            navController,
                            authViewModel
                        )
                    }

                    3 -> {

                        PengaturanPage(
                            Modifier.fillMaxSize(),
                            navController,
                            authViewModel,
                            darkMode,
                            onDarkModeChange
                        )
                    }
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
    var selectedTab by remember { mutableIntStateOf(0) }
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
                    icon = transactionCategoryIcon(item["icon"].toString()),
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

                Spacer(
                    Modifier.height(
                        24.dp
                    )
                )

                FinanceChart(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                )

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
                                Icons.AutoMirrored.Filled.TrendingUp,
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
                                Icons.AutoMirrored.Filled.TrendingDown,
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

fun formatRupiah(input: String): String {
    return input.reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerHome(
    authViewModel: AuthViewModel,
    initialMode: Int = 0,
    onSaved: () -> Unit
) {
    var mode by rememberSaveable { mutableIntStateOf(initialMode) } // 0 manual, 1 scan, 2 banyak, 3 voice
    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0 pengeluaran, 1 pemasukan
    var amount by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var selectedIcon by rememberSaveable { mutableStateOf("food") }
    var scanStatus by rememberSaveable { mutableStateOf("") }
    var receiptPreview by remember { mutableStateOf<List<String>>(emptyList()) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val bulkDrafts = remember { mutableStateListOf<TransactionDraft>() }
    val rawAmount = amount.replace(".", "").trim()
    val isFormValid = rawAmount.isNotEmpty() && desc.isNotBlank() && date.isNotBlank()
    val context = LocalContext.current
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    fun applyDraft(draft: TransactionDraft) {
        selectedTab = if (draft.type == "income") 1 else 0
        amount = if (draft.amount > 0) formatRupiah(draft.amount.toString()) else ""
        desc = draft.desc
        date = draft.date.ifBlank { formatDate(System.currentTimeMillis()) }
        selectedIcon = draft.icon.ifBlank { "food" }
    }

    fun createReceiptUri(): Uri {
        val dir = File(context.cacheDir, "receipts").apply { mkdirs() }
        val file = File.createTempFile("receipt_", ".jpg", dir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun processReceipt(uri: Uri) {
        scanStatus = "Membaca struk..."
        runCatching { InputImage.fromFilePath(context, uri) }
            .onSuccess { image ->
                recognizer.process(image)
                    .addOnSuccessListener { result ->

                        // Rekonstruksi teks: pasangkan elemen satu baris
                        // berdasarkan posisi Y yang berdekatan (toleransi 20px)
                        val lineMap = mutableMapOf<Int, MutableList<Pair<Int, String>>>()

                        result.textBlocks.forEach { block ->
                            block.lines.forEach { line ->
                                val centerY = line.boundingBox?.centerY() ?: return@forEach
                                val centerX = line.boundingBox?.centerX() ?: 0

                                // Cari bucket Y yang sudah ada dalam toleransi 20px
                                val bucketY = lineMap.keys.firstOrNull {
                                    kotlin.math.abs(it - centerY) < 20
                                } ?: centerY

                                lineMap.getOrPut(bucketY) { mutableListOf() }
                                    .add(Pair(centerX, line.text))
                            }
                        }

                        // Urutkan per baris (Y naik), lalu per kolom (X naik)
                        // Gabungkan jadi string — satu baris Y = satu baris teks
                        val reconstructedText = lineMap.entries
                            .sortedBy { it.key }
                            .joinToString("\n") { (_, items) ->
                                items.sortedBy { it.first }
                                    .joinToString(" ") { it.second }
                            }


                        val parsed = ReceiptParser.parse(
                            text = reconstructedText,
                            fallbackDate = formatDate(System.currentTimeMillis())
                        )

                        if (parsed == null) {
                            scanStatus = "Struk belum terbaca. Coba foto lebih jelas."
                        } else {
                            applyDraft(parsed.draft)
                            receiptPreview = parsed.itemLines
                            mode = 0
                            scanStatus = "Hasil scan sudah masuk ke form. Silakan cek lalu simpan."
                        }
                    }
                    .addOnFailureListener {
                        scanStatus = it.message ?: "Gagal membaca struk"
                    }
            }
            .onFailure {
                scanStatus = it.message ?: "Gagal membuka gambar"
            }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) processReceipt(uri)
        else scanStatus = "Pengambilan foto dibatalkan"
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = createReceiptUri()
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            scanStatus = "Izin kamera diperlukan untuk scan struk"
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) processReceipt(uri)
        else scanStatus = "Tidak ada gambar dipilih"
    }

    fun buildVoiceIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Contoh: beli makan 25000 hari ini kategori makanan")
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spoken = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()

        if (spoken.isNullOrBlank()) {
            Toast.makeText(context, "Voice tidak terbaca", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        val parsed = VoiceTransactionParser.parse(spoken, formatDate(System.currentTimeMillis()))
        if (parsed == null) {
            Toast.makeText(context, "Nominal voice belum terbaca", Toast.LENGTH_SHORT).show()
        } else {
            applyDraft(parsed)
            mode = 0
            Toast.makeText(context, "Voice masuk ke form. Silakan cek lagi.", Toast.LENGTH_SHORT).show()
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            voiceLauncher.launch(buildVoiceIntent())
        } else {
            Toast.makeText(context, "Izin mikrofon diperlukan untuk voice input", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestVoiceInput() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            voiceLauncher.launch(buildVoiceIntent())
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun requestCameraScan() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val uri = createReceiptUri()
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(Unit) {
        if (date.isBlank()) date = formatDate(System.currentTimeMillis())
        if (bulkDrafts.isEmpty()) {
            bulkDrafts.add(TransactionDraft(date = formatDate(System.currentTimeMillis()), icon = "food"))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            DrawerHeader(
                title = "Tambah Transaksi",
                subtitle = "Catat manual, scan struk, input banyak, atau pakai voice."
            )
        }

        item {
            ModeSelector(
                selectedMode = mode,
                onModeChange = { mode = it },
                modifier = Modifier.formWidth()
            )
            Spacer(Modifier.height(14.dp))
        }

        if (mode == 1) {
            item {
                ScanReceiptSection(
                    scanStatus = scanStatus,
                    receiptPreview = receiptPreview,
                    onCameraClick = { requestCameraScan() },
                    onGalleryClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.formWidth()
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        if (mode == 2) {
            item {
                BulkTransactionSection(
                    drafts = bulkDrafts,
                    onDraftChange = { index, draft -> bulkDrafts[index] = draft },
                    onAddRow = {
                        bulkDrafts.add(
                            TransactionDraft(
                                date = date.ifBlank { formatDate(System.currentTimeMillis()) },
                                type = if (selectedTab == 1) "income" else "expense",
                                icon = selectedIcon
                            )
                        )
                    },
                    onRemoveRow = { index ->
                        if (bulkDrafts.size > 1) bulkDrafts.removeAt(index)
                    },
                    onSaveAll = {
                        authViewModel.addTransactions(bulkDrafts.toList()) {
                            Toast.makeText(context, "Semua transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
                            onSaved()
                        }
                    },
                    modifier = Modifier.formWidth()
                )
                Spacer(Modifier.height(30.dp))
            }
            return@LazyColumn
        }

        if (mode == 3) {
            item {
                VoiceInputCard(
                    onVoiceClick = { requestVoiceInput() },
                    modifier = Modifier.formWidth()
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        item {
            Card(
                modifier = Modifier.formWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TransactionTypeSelector(
                        selectedTab = selectedTab,
                        onSelected = { selectedTab = it }
                    )

                    AmountField(
                        amount = amount,
                        selectedTab = selectedTab,
                        onAmountChange = { amount = it },
                        onVoiceClick = { requestVoiceInput() }
                    )

                    QuickAmountChips(
                        onAmountSelected = { amount = formatRupiah(it.toString()) }
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Deskripsi") },
                        placeholder = { Text("Contoh: Makan siang, gaji, bensin") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { requestVoiceInput() }) {
                                Icon(Icons.Default.Mic, contentDescription = "Isi dengan voice")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DatePickerField(
                        date = date,
                        onDateSelected = { date = it }
                    )

                    CategoryDropdown(
                        selectedIcon = selectedIcon,
                        onSelected = { selectedIcon = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val safeAmount = amount.replace(".", "").trim().toIntOrNull() ?: 0

                            authViewModel.addTransaction(
                                amount = safeAmount,
                                desc = desc.trim(),
                                date = date,
                                type = if (selectedTab == 1) "income" else "expense",
                                iconName = selectedIcon
                            )

                            Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            onSaved()
                        },
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid) bluelogo else Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text(
                            if (selectedTab == 0) "Simpan Pengeluaran"
                            else "Simpan Pemasukan",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun DrawerHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .formWidth()
            .padding(top = 4.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Spacer(Modifier.height(18.dp))
        Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ModeSelector(
    selectedMode: Int,
    onModeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        "Manual" to Icons.Default.Edit,
        "Scan" to Icons.Default.Image,
        "Banyak" to Icons.Default.Add,
        "Voice" to Icons.Default.Mic
    )

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modes.forEachIndexed { index, item ->
            val selected = selectedMode == index
            OutlinedButton(
                onClick = { onModeChange(index) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = item.second,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = item.first,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun TransactionTypeSelector(
    selectedTab: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TypeTab(
            text = "Pengeluaran",
            selected = selectedTab == 0,
            color = colorExpense,
            modifier = Modifier.weight(1f),
            onClick = { onSelected(0) }
        )
        TypeTab(
            text = "Pemasukan",
            selected = selectedTab == 1,
            color = colorIncome,
            modifier = Modifier.weight(1f),
            onClick = { onSelected(1) }
        )
    }
}

@Composable
private fun TypeTab(
    text: String,
    selected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) color.copy(alpha = 0.15f) else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        textAlign = TextAlign.Center,
        color = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
}

@Composable
private fun AmountField(
    amount: String,
    selectedTab: Int,
    onAmountChange: (String) -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isError by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    OutlinedTextField(
        value = amount,
        onValueChange = { newValue ->
            val raw = newValue.replace(".", "").trim()

            when {
                raw.isEmpty() -> {
                    isError = false
                    onAmountChange("")
                }
                raw.all(Char::isDigit) -> {
                    isError = false
                    onAmountChange(formatRupiah(raw))
                }
                else -> {
                    isError = true
                    Toast.makeText(context, "Hanya angka!", Toast.LENGTH_SHORT).show()
                }
            }
        },
        label = {
            Text(
                if (selectedTab == 0) "Jumlah Pengeluaran"
                else "Jumlah Pemasukan"
            )
        },
        placeholder = { Text("0") },
        supportingText = {
            if (isError) Text("Nominal hanya boleh angka")
            else Text("Titik ribuan akan otomatis ditambahkan")
        },
        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onVoiceClick) {
                Icon(Icons.Default.Mic, contentDescription = "Isi nominal dengan voice")
            }
        },
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun QuickAmountChips(
    onAmountSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAmounts = listOf(
        "10rb" to 10_000,
        "25rb" to 25_000,
        "50rb" to 50_000,
        "100rb" to 100_000
    )

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        quickAmounts.forEach { (label, value) ->
            OutlinedButton(
                onClick = { onAmountSelected(value) },
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(label, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    date: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val todayMillis = System.currentTimeMillis()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)

    OutlinedTextField(
        value = date,
        onValueChange = { },
        label = { Text("Tanggal") },
        placeholder = { Text(formatDate(todayMillis)) },
        readOnly = true,
        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
        trailingIcon = {
            TextButton(onClick = { showDatePicker = true }) {
                Text("Pilih")
            }
        },
        supportingText = {
            Text("Default hari ini. Tekan Pilih untuk mengganti tanggal.")
        },
        modifier = modifier.fillMaxWidth()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(formatDate(millis))
                        }
                        showDatePicker = false
                    }
                ) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedIcon: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedCategory = transactionCategories.firstOrNull { it.key == selectedIcon }
        ?: transactionCategories.first()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCategory.label,
            onValueChange = { },
            readOnly = true,
            label = { Text("Kategori") },
            leadingIcon = {
                Icon(
                    imageVector = selectedCategory.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(type =
                    MenuAnchorType.PrimaryNotEditable,
                    enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            transactionCategories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.label) },
                    leadingIcon = {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = if (category.key == selectedIcon) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = {
                        onSelected(category.key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ScanReceiptSection(
    scanStatus: String,
    receiptPreview: List<String>,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Scan Struk", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                "Foto atau pilih gambar struk. Hasilnya akan otomatis masuk ke form manual.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onCameraClick
                ) {
                    Text("Kamera")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onGalleryClick
                ) {
                    Text("Galeri")
                }
            }

            if (scanStatus.isNotBlank()) {
                Text(
                    text = scanStatus,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (receiptPreview.isNotEmpty()) {
                HorizontalDivider()
                Text("Preview item terbaca", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                receiptPreview.take(5).forEach {
                    Text("• $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (receiptPreview.size > 5) {
                    Text(
                        "+${receiptPreview.size - 5} item lainnya",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceInputCard(
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text("Voice Input", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Text(
                "Ucapkan transaksi sekali saja, misalnya: beli makan 25000 hari ini kategori makanan.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onVoiceClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mulai Voice Input")
            }
        }
    }
}

@Composable
private fun BulkTransactionSection(
    drafts: MutableList<TransactionDraft>,
    onDraftChange: (Int, TransactionDraft) -> Unit,
    onAddRow: () -> Unit,
    onRemoveRow: (Int) -> Unit,
    onSaveAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBulkValid = drafts.isNotEmpty() && drafts.all {
        it.amount > 0 && it.desc.isNotBlank() && it.date.isNotBlank()
    }

    Column(modifier = modifier) {
        Text("Input Banyak Transaksi", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            "Cocok untuk mencatat banyak transaksi kecil. Tanggal, tipe, dan kategori bisa dipilih cepat.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        drafts.forEachIndexed { index, draft ->
            BulkTransactionCard(
                index = index,
                draft = draft,
                canRemove = drafts.size > 1,
                onDraftChange = { updated -> onDraftChange(index, updated) },
                onRemove = { onRemoveRow(index) }
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onAddRow,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Tambah Baris")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onSaveAll,
            enabled = isBulkValid,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isBulkValid) bluelogo else Color.Gray
            )
        ) {
            Text("Simpan Semua", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BulkTransactionCard(
    index: Int,
    draft: TransactionDraft,
    canRemove: Boolean,
    onDraftChange: (TransactionDraft) -> Unit,
    onRemove: () -> Unit
) {
    val rowAmount = if (draft.amount > 0) formatRupiah(draft.amount.toString()) else ""
    val rowType = if (draft.type == "income") 1 else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi ${index + 1}",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (canRemove) {
                    TextButton(onClick = onRemove) {
                        Text("Hapus", color = colorExpense)
                    }
                }
            }

            OutlinedTextField(
                value = draft.desc,
                onValueChange = { onDraftChange(draft.copy(desc = it)) },
                label = { Text("Deskripsi") },
                placeholder = { Text("Contoh: Es teh, parkir, bensin") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rowAmount,
                onValueChange = { value ->
                    val raw = value.replace(".", "").trim()
                    if (raw.all(Char::isDigit)) {
                        onDraftChange(draft.copy(amount = raw.toIntOrNull() ?: 0))
                    }
                },
                label = { Text("Nominal") },
                placeholder = { Text("0") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DateTextFieldForBulk(
                date = draft.date.ifBlank { formatDate(System.currentTimeMillis()) },
                onDateChange = { onDraftChange(draft.copy(date = it)) }
            )

            TransactionTypeSelector(
                selectedTab = rowType,
                onSelected = { selected ->
                    onDraftChange(draft.copy(type = if (selected == 1) "income" else "expense"))
                }
            )

            CategoryDropdown(
                selectedIcon = draft.icon.ifBlank { "food" },
                onSelected = { onDraftChange(draft.copy(icon = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DateTextFieldForBulk(
    date: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = date,
        onValueChange = onDateChange,
        label = { Text("Tanggal") },
        placeholder = { Text(formatDate(System.currentTimeMillis())) },
        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
        singleLine = true,
        supportingText = { Text("Default hari ini. Bisa disamakan untuk semua baris.") },
        modifier = modifier.fillMaxWidth()
    )
}

// CategoryPicker lama tetap disediakan supaya komponen lama tidak hilang.
// Manual form dan bulk form sekarang memakai CategoryDropdown agar user tidak perlu melihat grid panjang.
private fun Modifier.formWidth(): Modifier {
    return this
        .widthIn(max = 560.dp)
        .fillMaxWidth()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("unused")
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

fun formatDate(millis: Long): String {

    val locale =
        Locale.Builder()
            .setLanguage("id")
            .setRegion("ID")
            .build()

    val sdf =
        SimpleDateFormat(
            "dd MMM yyyy",
            locale
        )

    return sdf.format(
        Date(millis)
    )
}
