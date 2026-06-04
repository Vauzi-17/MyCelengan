package com.mycelengan.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycelengan.AuthViewModel
import com.mycelengan.formatSignedRupiah
import com.mycelengan.transactionCategoryIcon
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome

data class TransactionUi(
    val title: String,
    val date: String,
    val amount: String,
    val income: Boolean,
    val icon: String
)

@Composable
fun TransaksiPage(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {

    var selectedTab by remember {
        mutableStateOf(0)
    }

    val transactions =
        authViewModel.transactions.observeAsState(emptyList())

    val data =
        transactions.value.map { item ->
            val income =
                item["type"] == "income"

            TransactionUi(
                title =
                    item["desc"]
                        ?.toString()
                        .orEmpty()
                        .ifBlank { "Transaksi" },
                date =
                    item["date"]
                        ?.toString()
                        .orEmpty(),
                amount =
                    formatSignedRupiah(item["type"].toString(), item["amount"]),
                income =
                    income,
                icon =
                    item["icon"]
                        ?.toString()
                        .orEmpty()
            )
        }

    val filtered = when (
        selectedTab
    ) {

        1 ->
            data.filter {
                it.income
            }

        2 ->
            data.filter {
                !it.income
            }

        else ->
            data
    }

    LazyColumn(

        modifier =
            modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp
                ),

        contentPadding =
            PaddingValues(bottom = 120.dp),

        verticalArrangement =
            Arrangement.spacedBy(
                12.dp
            )

    ) {

        item {

            Card(

                shape =
                    RoundedCornerShape(
                        22.dp
                    ),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surfaceContainerLow
                    )

            ) {

                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                20.dp
                            )

                ) {

                    Text(

                        "Riwayat Transaksi",

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            20.sp
                    )

                    Spacer(
                        Modifier.height(
                            8.dp
                        )
                    )

                    Text(

                        "${filtered.size} transaksi",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }

        item {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(

                            MaterialTheme
                                .colorScheme
                                .surfaceContainerLow,

                            RoundedCornerShape(
                                14.dp
                            )
                        )
                        .padding(
                            6.dp
                        )
            ) {

                listOf(
                    "Semua",
                    "Pemasukan",
                    "Pengeluaran"
                ).forEachIndexed {

                        index,
                        label ->

                    val selected =
                        selectedTab ==
                                index

                    Box(

                        modifier =
                            Modifier
                                .weight(
                                    1f
                                )
                                .background(

                                    if (
                                        selected
                                    )

                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                            .copy(
                                                .15f
                                            )

                                    else

                                        Color.Transparent,

                                    RoundedCornerShape(
                                        12.dp
                                    )
                                )
                                .clickable {

                                    selectedTab =
                                        index
                                }
                                .padding(
                                    12.dp
                                ),

                        contentAlignment =
                            Alignment.Center

                    ) {

                        Text(
                            label
                        )
                    }
                }
            }
        }

        if (
            filtered.isEmpty()
        ) {

            item {

                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 80.dp
                            ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally

                ) {

                    Icon(
                        Icons.Default.Info,
                        null
                    )

                    Spacer(
                        Modifier.height(
                            10.dp
                        )
                    )

                    Text(
                        "Belum ada transaksi"
                    )
                }
            }
        }

        items(
            filtered
        ) {

            Card(

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surface
                    )

            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                18.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Icon(

                        transactionCategoryIcon(it.icon),

                        null
                    )

                    Spacer(
                        Modifier.width(
                            16.dp
                        )
                    )

                    Column(

                        Modifier.weight(
                            1f
                        )

                    ) {

                        Text(
                            it.title
                        )

                        Text(

                            it.date,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Text(

                        it.amount,

                        fontWeight =
                            FontWeight.Bold,

                        color =

                            if (
                                it.income
                            )

                                colorIncome

                            else

                                colorExpense
                    )
                }

                HorizontalDivider()
            }
        }

        item {

            Spacer(
                Modifier.height(
                    120.dp
                )
            )
        }
    }
}
