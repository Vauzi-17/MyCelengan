package com.mycelengan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionDraft(
    val type: String = "expense",
    val amount: Int = 0,
    val desc: String = "",
    val date: String = "",
    val icon: String = "other"
)

data class TransactionCategory(
    val key: String,
    val label: String,
    val icon: ImageVector
)

val transactionCategories = listOf(
    TransactionCategory("food", "Makanan", Icons.Default.Fastfood),
    TransactionCategory("groceries", "Belanja", Icons.Default.LocalGroceryStore),
    TransactionCategory("transport", "Transport", Icons.Default.Train),
    TransactionCategory("shopping", "Shopping", Icons.Default.ShoppingCart),
    TransactionCategory("bills", "Tagihan", Icons.Default.ReceiptLong),
    TransactionCategory("health", "Kesehatan", Icons.Default.HealthAndSafety),
    TransactionCategory("education", "Edukasi", Icons.Default.School),
    TransactionCategory("salary", "Gaji", Icons.Default.Savings),
    TransactionCategory("saving", "Tabungan", Icons.Default.Savings),
    TransactionCategory("other", "Lainnya", Icons.Default.Edit)
)

fun transactionCategoryIcon(key: String): ImageVector {
    val normalized = when (key) {
        "fastfood" -> "food"
        "train" -> "transport"
        "money" -> "salary"
        "edit" -> "other"
        else -> key
    }
    return transactionCategories.firstOrNull { it.key == normalized }?.icon ?: Icons.Default.Edit
}
