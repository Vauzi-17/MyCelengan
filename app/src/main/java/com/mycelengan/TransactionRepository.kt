package com.mycelengan

import android.os.Handler
import android.os.Looper
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
                    val amount = parseRupiah(item["amount"])

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
        return transactionCategoryIcon(name)
    }

    fun formatAmount(type: String, amount: Int): String {
        return formatSignedRupiah(type, amount)
    }
}
