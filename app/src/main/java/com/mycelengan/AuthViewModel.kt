package com.mycelengan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    // ====================== TARGET LIST ======================
    private val _targets = MutableLiveData<List<TargetItem>>()
    val targets: LiveData<List<TargetItem>> = _targets

    private val _targetEntries = MutableLiveData<List<Map<String, Any>>>()
    val targetEntries: LiveData<List<Map<String, Any>>> = _targetEntries

    private var targetEntriesListenerRegistration: ListenerRegistration? = null

    private val _currentTarget = MutableLiveData<TargetItem?>()
    val currentTarget: LiveData<TargetItem?> = _currentTarget

    private val _targetHistory = MutableLiveData<List<Map<String, Any>>>()
    val targetHistory: LiveData<List<Map<String, Any>>> = _targetHistory


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
        loadTargetData(uid)
    }

    fun loadTargetData(uid: String) {
        db.collection("users")
            .document(uid)
            .collection("targets")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        TargetItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle") ?: "",
                            icon = doc.getString("icon") ?: "",
                            targetAmount = doc.getLong("targetAmount")?.toInt() ?: 0,
                            currentAmount = doc.getLong("currentAmount")?.toInt() ?: 0,
                            perMonth = doc.getLong("perMonth")?.toInt() ?: 0,
                            createdAt = doc.getString("createdAt") ?: ""
                        )
                    }

                    _targets.value = list
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

    fun addTarget(
        title: String,
        subtitle: String,
        icon: String,
        targetAmount: Int,
        perMonth: Int,
        createdAt: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.collection("users").document(uid)
            .collection("targets").document()

        val data = mapOf(
            "title" to title,
            "subtitle" to subtitle,
            "icon" to icon,
            "targetAmount" to targetAmount,
            "currentAmount" to 0,
            "perMonth" to perMonth,
            "createdAt" to createdAt,
            "timestamp" to FieldValue.serverTimestamp()
        )

        ref.set(data)
    }

    fun deleteTarget(targetId: String, onComplete: () -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: return

        val ref = db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)

        // Hapus target + subcollection history
        ref.collection("history").get().addOnSuccessListener { snap ->
            val batch = db.batch()

            snap.documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            batch.delete(ref)

            batch.commit().addOnSuccessListener {
                onComplete()
            }
        }
    }

    fun updateTargetName(targetId: String, newName: String, onComplete: () -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)
            .update("title", newName)
            .addOnSuccessListener { onComplete() }
    }


    fun getTargetById(targetId: String, onResult: (Map<String, Any>?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.data)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun updateTargetProgress(
        targetId: String,
        amount: Int,
        isAdd: Boolean,
        desc: String,
        onSuccess: () -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        val targetRef = db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)

        db.runTransaction { tr ->
            val doc = tr.get(targetRef)
            val oldAmount = doc.getLong("currentAmount")?.toInt() ?: 0

            val newAmount =
                if (isAdd) oldAmount + amount
                else oldAmount - amount

            // update amount
            tr.update(targetRef, "currentAmount", newAmount)

            // add history
            val historyRef = targetRef.collection("history").document()
            tr.set(historyRef, mapOf(
                "amount" to amount,
                "type" to if (isAdd) "add" else "minus",
                "desc" to desc,
                "timestamp" to FieldValue.serverTimestamp()
            ))
        }.addOnSuccessListener { onSuccess() }
    }


    fun getTargetDetailRealtime(
        targetId: String,
        onChange: (TargetItem?) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)
            .addSnapshotListener { doc, _ ->
                if (doc != null && doc.exists()) {
                    val item = TargetItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        subtitle = doc.getString("subtitle") ?: "",
                        icon = doc.getString("icon") ?: "",
                        targetAmount = doc.getLong("targetAmount")?.toInt() ?: 0,
                        currentAmount = doc.getLong("currentAmount")?.toInt() ?: 0,
                        perMonth = doc.getLong("perMonth")?.toInt() ?: 0,
                        createdAt = doc.getString("createdAt") ?: ""
                    )
                    onChange(item)
                } else {
                    onChange(null)
                }
            }
    }

    fun getTargetHistoryRealtime(
        targetId: String,
        onChange: (List<Map<String, Any>>) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)
            .collection("history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.data }
                    onChange(list)
                }
            }
    }

    // --- fungsi untuk subscribe ke entries target (realtime) ---
    fun subscribeTargetEntries(targetId: String) {
        // buang listener lama kalau ada
        targetEntriesListenerRegistration?.remove()

        val uid = auth.currentUser?.uid ?: return
        val entriesRef = db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)
            .collection("entries")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        targetEntriesListenerRegistration = entriesRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["id"] = doc.id
                    data
                }
                _targetEntries.value = list
            } else {
                _targetEntries.value = emptyList()
            }
        }
    }

    fun unsubscribeTargetEntries() {
        targetEntriesListenerRegistration?.remove()
        targetEntriesListenerRegistration = null
        _targetEntries.value = emptyList()
    }

    fun addTargetEntry(targetId: String, amount: Int, desc: String, isAdd: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val targetRef = db.collection("users").document(uid).collection("targets").document(targetId)
        val entriesCol = targetRef.collection("entries")

        // Gunakan transaction untuk update currentAmount dan menambah entry atomically
        db.runTransaction { tr ->
            val targetDoc = tr.get(targetRef)
            val oldCurrent = targetDoc.getLong("currentAmount")?.toInt() ?: 0

            val newCurrent = if (isAdd) oldCurrent + amount else (oldCurrent - amount).coerceAtLeast(0)
            tr.update(targetRef, mapOf("currentAmount" to newCurrent))

            val entryData = mapOf(
                "amount" to amount,
                "desc" to desc,
                "type" to if (isAdd) "add" else "remove",
                "timestamp" to FieldValue.serverTimestamp()
            )
            val newEntryRef = entriesCol.document()
            tr.set(newEntryRef, entryData)
        }.addOnSuccessListener {
            Log.d("AuthViewModel", "Entry berhasil ditambah dan currentAmount terupdate")
        }.addOnFailureListener { e ->
            Log.e("AuthViewModel", "Gagal menambah entry target: ${e.message}")
        }
    }

    fun subscribeTargetDetail(targetId: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)
            .addSnapshotListener { doc, _ ->
                if (doc != null && doc.exists()) {
                    _currentTarget.value = TargetItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        subtitle = doc.getString("subtitle") ?: "",
                        icon = doc.getString("icon") ?: "",
                        targetAmount = doc.getLong("targetAmount")?.toInt() ?: 0,
                        currentAmount = doc.getLong("currentAmount")?.toInt() ?: 0,
                        perMonth = doc.getLong("perMonth")?.toInt() ?: 0,
                        createdAt = doc.getString("createdAt") ?: ""
                    )
                }
            }
    }

    fun subscribeTargetHistory(targetId: String) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("targets")
            .document(targetId)
            .collection("history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        val data = doc.data ?: emptyMap()
                        data
                    }
                    _targetHistory.value = list
                }
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

