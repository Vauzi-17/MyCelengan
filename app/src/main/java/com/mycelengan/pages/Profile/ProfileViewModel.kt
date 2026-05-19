package com.mycelengan

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycelengan.api.LaravelApi
import java.util.concurrent.Executors

class ProfileViewModel : ViewModel() {

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _updateState = MutableLiveData<String?>()
    val updateState: LiveData<String?> = _updateState

    fun updateUsername(newName: String, authViewModel: AuthViewModel) {
        runApi(
            task = { LaravelApi.updateProfile(newName).toMap() },
            onSuccess = { user ->
                _updateState.value = "Username updated"
                authViewModel.setUsername(user["username"].toString())
                authViewModel.loadUserData()
            },
            onError = { _updateState.value = it }
        )
    }

    fun changePassword(newPassword: String) {
        runApi(
            task = {
                LaravelApi.changePassword(newPassword)
                Unit
            },
            onSuccess = { _updateState.value = "Password updated" },
            onError = { _updateState.value = it }
        )
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
}
