package com.mycelengan

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mycelengan.api.LaravelApi
import com.mycelengan.api.TargetDto
import java.util.concurrent.Executors

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

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

    private val _targets = MutableLiveData<List<TargetItem>>()
    val targets: LiveData<List<TargetItem>> = _targets

    private val _targetEntries = MutableLiveData<List<Map<String, Any>>>()
    val targetEntries: LiveData<List<Map<String, Any>>> = _targetEntries

    private val _currentTarget = MutableLiveData<TargetItem?>()
    val currentTarget: LiveData<TargetItem?> = _currentTarget

    private val _targetHistory = MutableLiveData<List<Map<String, Any>>>()
    val targetHistory: LiveData<List<Map<String, Any>>> = _targetHistory

    private val _userData = MutableLiveData<Map<String, Any>>()
    val userData: LiveData<Map<String, Any>> = _userData

    init {
        LaravelApi.init(application)
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (LaravelApi.hasToken()) {
            _authState.value = AuthState.Authenticated
            loadUserData()
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        runApi(
            task = {
                val response = LaravelApi.login(email, password)
                LaravelApi.saveToken(response.token)
                response.user.toMap()
            },
            onSuccess = { user ->
                applyUser(user)
                _authState.value = AuthState.Authenticated
                refreshAll()
            },
            onError = { _authState.value = AuthState.Error(it) }
        )
    }

    fun signup(email: String, password: String, username: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            _authState.value = AuthState.Error("All fields must be filled")
            return
        }

        _authState.value = AuthState.Loading
        runApi(
            task = {
                val response = LaravelApi.register(email, password, username)
                LaravelApi.saveToken(response.token)
                response.user.toMap()
            },
            onSuccess = { user ->
                applyUser(user)
                _authState.value = AuthState.Authenticated
                refreshAll()
            },
            onError = { _authState.value = AuthState.Error(it) }
        )
    }

    fun signout() {
        runApi(
            task = {
                LaravelApi.logout()
                Unit
            },
            onSuccess = {
                clearLocalData()
                _authState.value = AuthState.Unauthenticated
            },
            onError = {
                LaravelApi.clearToken()
                clearLocalData()
                _authState.value = AuthState.Unauthenticated
            }
        )
    }

    fun loadUserData() {
        if (!LaravelApi.hasToken()) return
        refreshAll()
    }

    fun loadTargetData(uid: String = "") {
        if (!LaravelApi.hasToken()) return
        runApi(
            task = { LaravelApi.targets().map { it.toTargetItem() } },
            onSuccess = { _targets.value = it },
            onError = { Log.e("AuthViewModel", "Gagal load target: $it") }
        )
    }

    fun addTransaction(
        amount: Int,
        desc: String,
        date: String,
        type: String,
        iconName: String
    ) {
        runApi(
            task = {
                LaravelApi.addTransaction(amount, desc, date, type, iconName)
                Unit
            },
            onSuccess = { refreshAll() },
            onError = { _authState.value = AuthState.Error(it) }
        )
    }

    fun deleteTransaction(transactionId: String, amount: Int, type: String) {
        runApi(
            task = {
                LaravelApi.deleteTransaction(transactionId)
                Unit
            },
            onSuccess = { refreshAll() },
            onError = { Log.e("AuthViewModel", "Gagal menghapus transaksi: $it") }
        )
    }

    fun addTarget(
        title: String,
        subtitle: String,
        icon: String,
        targetAmount: Int,
        perMonth: Int,
        createdAt: String
    ) {
        runApi(
            task = {
                LaravelApi.addTarget(title, subtitle, icon, targetAmount, perMonth, createdAt)
                Unit
            },
            onSuccess = { loadTargetData() },
            onError = { _authState.value = AuthState.Error(it) }
        )
    }

    fun deleteTarget(targetId: String, onComplete: () -> Unit = {}) {
        runApi(
            task = {
                LaravelApi.deleteTarget(targetId)
                Unit
            },
            onSuccess = {
                loadTargetData()
                onComplete()
            },
            onError = { Log.e("AuthViewModel", "Gagal menghapus target: $it") }
        )
    }

    fun updateTargetName(targetId: String, newName: String, onComplete: () -> Unit = {}) {
        runApi(
            task = {
                LaravelApi.updateTargetName(targetId, newName)
                Unit
            },
            onSuccess = {
                loadTargetData()
                subscribeTargetDetail(targetId)
                onComplete()
            },
            onError = { Log.e("AuthViewModel", "Gagal update target: $it") }
        )
    }

    fun getTargetById(targetId: String, onResult: (Map<String, Any>?) -> Unit) {
        runApi(
            task = { LaravelApi.target(targetId).toTargetItem().toMap() },
            onSuccess = { onResult(it) },
            onError = { onResult(null) }
        )
    }

    fun updateTargetProgress(
        targetId: String,
        amount: Int,
        isAdd: Boolean,
        desc: String,
        onSuccess: () -> Unit
    ) {
        runApi(
            task = {
                LaravelApi.addTargetEntry(targetId, amount, desc, isAdd)
                Unit
            },
            onSuccess = {
                subscribeTargetDetail(targetId)
                subscribeTargetHistory(targetId)
                loadTargetData()
                onSuccess()
            },
            onError = { _authState.value = AuthState.Error(it) }
        )
    }

    fun getTargetDetailRealtime(targetId: String, onChange: (TargetItem?) -> Unit) {
        runApi(
            task = { LaravelApi.target(targetId).toTargetItem() },
            onSuccess = { onChange(it) },
            onError = { onChange(null) }
        )
    }

    fun getTargetHistoryRealtime(targetId: String, onChange: (List<Map<String, Any>>) -> Unit) {
        runApi(
            task = { LaravelApi.targetHistory(targetId) },
            onSuccess = { onChange(it) },
            onError = { onChange(emptyList()) }
        )
    }

    fun subscribeTargetEntries(targetId: String) {
        runApi(
            task = { LaravelApi.targetEntries(targetId) },
            onSuccess = { _targetEntries.value = it },
            onError = { _targetEntries.value = emptyList() }
        )
    }

    fun unsubscribeTargetEntries() {
        _targetEntries.value = emptyList()
    }

    fun addTargetEntry(targetId: String, amount: Int, desc: String, isAdd: Boolean) {
        updateTargetProgress(targetId, amount, isAdd, desc) {
            Log.d("AuthViewModel", "Entry berhasil ditambah")
        }
    }

    fun subscribeTargetDetail(targetId: String) {
        runApi(
            task = { LaravelApi.target(targetId).toTargetItem() },
            onSuccess = { _currentTarget.value = it },
            onError = { _currentTarget.value = null }
        )
    }

    fun subscribeTargetHistory(targetId: String) {
        runApi(
            task = { LaravelApi.targetHistory(targetId) },
            onSuccess = { _targetHistory.value = it },
            onError = { _targetHistory.value = emptyList() }
        )
    }

    fun setUsername(newName: String) {
        val current = _userData.value?.toMutableMap() ?: mutableMapOf()
        current["username"] = newName
        current["photoUrl"] = newName.firstOrNull()?.uppercase().orEmpty()
        _userData.value = current
    }

    private fun refreshAll() {
        runApi(
            task = {
                val user = LaravelApi.me().toMap()
                val transactions = LaravelApi.transactions()
                val targets = LaravelApi.targets().map { it.toTargetItem() }
                Triple(user, transactions, targets)
            },
            onSuccess = { (user, transactions, targets) ->
                applyUser(user)
                _transactions.value = transactions
                _targets.value = targets
            },
            onError = { message ->
                if (message.contains("Unauthenticated", ignoreCase = true)) {
                    LaravelApi.clearToken()
                    clearLocalData()
                    _authState.value = AuthState.Unauthenticated
                } else {
                    Log.e("AuthViewModel", "Gagal refresh data: $message")
                }
            }
        )
    }

    private fun applyUser(user: Map<String, Any>) {
        _userData.value = user
        _saldo.value = (user["saldo"] as? Number)?.toInt() ?: 0
        _totalIncome.value = (user["totalIncome"] as? Number)?.toInt() ?: 0
        _totalExpense.value = (user["totalExpense"] as? Number)?.toInt() ?: 0
    }

    private fun clearLocalData() {
        _userData.value = emptyMap()
        _saldo.value = 0
        _totalIncome.value = 0
        _totalExpense.value = 0
        _transactions.value = emptyList()
        _targets.value = emptyList()
        _currentTarget.value = null
        _targetHistory.value = emptyList()
        _targetEntries.value = emptyList()
    }

    private fun <T> runApi(
        task: () -> T,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) {
        executor.execute {
            try {
                val result = task()
                mainHandler.post { onSuccess(result) }
            } catch (e: Exception) {
                mainHandler.post { onError(e.message ?: "Terjadi kesalahan") }
            }
        }
    }

    private fun TargetDto.toTargetItem(): TargetItem {
        return TargetItem(
            id = id,
            title = title,
            subtitle = subtitle,
            icon = icon,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            perMonth = perMonth,
            createdAt = createdAt
        )
    }

    private fun TargetItem.toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "subtitle" to subtitle,
        "icon" to icon,
        "targetAmount" to targetAmount,
        "currentAmount" to currentAmount,
        "perMonth" to perMonth,
        "createdAt" to createdAt
    )
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    data class Success(val message: String) : AuthState()
}
