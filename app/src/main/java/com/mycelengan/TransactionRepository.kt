package com.mycelengan

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mycelengan.pages.TransactionItem
import com.mycelengan.ui.theme.colorExpense
import com.mycelengan.ui.theme.colorIncome

class TransactionRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun uid() = auth.currentUser!!.uid

    fun addTransaction(
        type: String,
        amount: Int,
        desc: String,
        date: String,
        iconName: String,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        val txId = db.collection("users")
            .document(uid())
            .collection("transactions")
            .document().id

        val txData = mapOf(
            "type" to type,
            "amount" to amount,
            "desc" to desc,
            "date" to date,
            "icon" to iconName,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        val userRef = db.collection("users").document(uid())
        val txRef = userRef.collection("transactions").document(txId)

        db.runTransaction { t ->

            val user = t.get(userRef)
            val saldo = user.getLong("saldo") ?: 0
            val income = user.getLong("totalIncome") ?: 0
            val expense = user.getLong("totalExpense") ?: 0

            val newSaldo = if (type == "income") saldo + amount else saldo - amount
            val newIncome = if (type == "income") income + amount else income
            val newExpense = if (type == "expense") expense + amount else expense

            t.set(txRef, txData)
            t.update(userRef, mapOf(
                "saldo" to newSaldo,
                "totalIncome" to newIncome,
                "totalExpense" to newExpense
            ))
        }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail(it.message ?: "Error") }
    }

    fun listenTransactions(onChange: (List<TransactionItem>) -> Unit) {
        db.collection("users")
            .document(uid())
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->

                if (snap == null) return@addSnapshotListener

                val list = snap.documents.map { doc ->
                    val iconName = doc.getString("icon") ?: "fastfood"

                    TransactionItem(
                        icon = iconFromName(iconName),
                        title = doc.getString("desc") ?: "",
                        date = doc.getString("date") ?: "",
                        amount = formatAmount(doc.getString("type")!!, doc.getLong("amount")!!.toInt()),
                        amountColor = if (doc.getString("type") == "income") colorIncome else colorExpense
                    )
                }

                onChange(list)
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
