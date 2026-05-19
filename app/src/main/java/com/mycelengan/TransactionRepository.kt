package com.mycelengan

import android.os.Handler
import android.os.Looper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import com.mycelengan.api.LaravelApi
import com.mycelengan.pages.TransactionItem
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome
import java.util.concurrent.Executors

class TransactionRepository {

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun addTransaction(
        type: String,
        amount: Int,
        desc: String,
        date: String,
        iconName: String,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        executor.execute {
            try {
                LaravelApi.addTransaction(amount, desc, date, type, iconName)
                mainHandler.post { onSuccess() }
            } catch (e: Exception) {
                mainHandler.post { onFail(e.message ?: "Error") }
            }
        }
    }

    fun listenTransactions(onChange: (List<TransactionItem>) -> Unit) {
        executor.execute {
            try {
                val list = LaravelApi.transactions().map { item ->
                    val iconName = item["icon"].toString()
                    val type = item["type"].toString()
                    val amount = item["amount"].toString().toIntOrNull() ?: 0

                    TransactionItem(
                        icon = iconFromName(iconName),
                        title = item["desc"].toString(),
                        date = item["date"].toString(),
                        amount = formatAmount(type, amount),
                        amountColor = if (type == "income") colorIncome else colorExpense
                    )
                }
                mainHandler.post { onChange(list) }
            } catch (_: Exception) {
                mainHandler.post { onChange(emptyList()) }
            }
        }
    }

    fun iconFromName(name: String): ImageVector {
        return when (name) {
            "fastfood" -> Icons.Default.Fastfood
            "shopping" -> Icons.Default.ShoppingCart
            "train" -> Icons.Default.Train
            "money" -> Icons.Default.AttachMoney
            "edit" -> Icons.Default.Edit
            else -> Icons.Default.Fastfood
        }
    }

    fun formatAmount(type: String, amount: Int): String {
        return if (type == "income") "+Rp $amount" else "-Rp $amount"
    }
}
