package com.mycelengan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _saldo = MutableLiveData<Int>()
    val saldo: LiveData<Int> = _saldo

    private val _totalIncome = MutableLiveData<Int>()
    val totalIncome: LiveData<Int> = _totalIncome

    private val _totalExpense = MutableLiveData<Int>()
    val totalExpense: LiveData<Int> = _totalExpense

    private val _transactions = MutableLiveData<List<Map<String, Any>>>()
    val transactions: LiveData<List<Map<String, Any>>> = _transactions

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            loadUserData()
        }
    }

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Authenticated
                loadUserData()
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Something went wrong")
            }
    }

    fun signup(email: String, password: String, username: String) {

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            _authState.value = AuthState.Error("All fields must be filled")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser!!.uid
                val initial = username.first().uppercaseChar()

                val userData = mapOf(
                    "uid" to uid,
                    "email" to email,
                    "username" to username,
                    "photoUrl" to initial.toString(),
                    "saldo" to 0,
                    "totalIncome" to 0,
                    "totalExpense" to 0
                )

                db.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        _authState.value = AuthState.Authenticated
                        loadUserData()
                    }
                    .addOnFailureListener {
                        _authState.value = AuthState.Error("Failed to save user")
                    }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Something went wrong")
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    private val _userData = MutableLiveData<Map<String, Any>>()
    val userData: LiveData<Map<String, Any>> = _userData

    fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        // user data realtime
        db.collection("users").document(uid)
            .addSnapshotListener { doc, _ ->
                if (doc != null && doc.exists()) {
                    _userData.value = doc.data
                    _saldo.value = doc.getLong("saldo")?.toInt() ?: 0
                    _totalIncome.value = doc.getLong("totalIncome")?.toInt() ?: 0
                    _totalExpense.value = doc.getLong("totalExpense")?.toInt() ?: 0
                }
            }

        // transaksi realtime (DESCENDING)
        // transaksi realtime (DESCENDING) - UPDATE BAGIAN INI
        db.collection("users")
            .document(uid)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        val data = doc.data?.toMutableMap() ?: mutableMapOf()
                        data["id"] = doc.id // Tambahkan ID dokumen
                        data
                    }
                    _transactions.value = list
                }
            }
        }

    fun addTransaction(
        amount: Int,
        desc: String,
        date: String,
        type: String,
        iconName: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(uid)

        db.runTransaction { tr ->

            val doc = tr.get(userRef)
            val oldSaldo = doc.getLong("saldo")?.toInt() ?: 0
            val oldIncome = doc.getLong("totalIncome")?.toInt() ?: 0
            val oldExpense = doc.getLong("totalExpense")?.toInt() ?: 0

            val newSaldo = if (type == "income") oldSaldo + amount else oldSaldo - amount
            val newIncome = if (type == "income") oldIncome + amount else oldIncome
            val newExpense = if (type == "expense") oldExpense + amount else oldExpense

            tr.update(
                userRef,
                mapOf(
                    "saldo" to newSaldo,
                    "totalIncome" to newIncome,
                    "totalExpense" to newExpense
                )
            )

            val transRef = userRef.collection("transactions").document()
            tr.set(
                transRef,
                mapOf(
                    "amount" to amount,
                    "desc" to desc,
                    "date" to date,
                    "type" to type,
                    "icon" to iconName,
                    "timestamp" to FieldValue.serverTimestamp()
                )
            )
        }
    }

    fun deleteTransaction(transactionId: String, amount: Int, type: String) {
        val uid = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(uid)

        db.runTransaction { tr ->
            val doc = tr.get(userRef)
            val oldSaldo = doc.getLong("saldo")?.toInt() ?: 0
            val oldIncome = doc.getLong("totalIncome")?.toInt() ?: 0
            val oldExpense = doc.getLong("totalExpense")?.toInt() ?: 0

            // Kembalikan nilai sesuai tipe transaksi
            val newSaldo = if (type == "income") oldSaldo - amount else oldSaldo + amount
            val newIncome = if (type == "income") oldIncome - amount else oldIncome
            val newExpense = if (type == "expense") oldExpense - amount else oldExpense

            tr.update(
                userRef,
                mapOf(
                    "saldo" to newSaldo,
                    "totalIncome" to newIncome,
                    "totalExpense" to newExpense
                )
            )

            // Hapus dokumen transaksi
            val transRef = userRef.collection("transactions").document(transactionId)
            tr.delete(transRef)
        }.addOnSuccessListener {
            Log.d("AuthViewModel", "Transaksi berhasil dihapus")
        }.addOnFailureListener { e ->
            Log.e("AuthViewModel", "Gagal menghapus transaksi: ${e.message}")
        }
    }

    fun setUsername(newName: String) {
        val current = _userData.value?.toMutableMap() ?: mutableMapOf()
        current["username"] = newName
        _userData.value = current
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    data class Success(val message: String) : AuthState()
}

