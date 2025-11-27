package com.mycelengan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _updateState = MutableLiveData<String?>()
    val updateState: LiveData<String?> = _updateState

    private val uid: String get() = auth.currentUser!!.uid

    // -------------------------
    // UPDATE USERNAME
    // -------------------------
    fun updateUsername(newName: String, authViewModel: AuthViewModel) {
        db.collection("users").document(uid)
            .update("username", newName)
            .addOnSuccessListener {
                _updateState.value = "Username updated"

                // ⬇⬇⬇  Kunci agar UI langsung berubah
                authViewModel.setUsername(newName)
            }
            .addOnFailureListener {
                _updateState.value = it.message
            }
    }


    // -------------------------
    // CHANGE PASSWORD
    // -------------------------
    fun changePassword(newPassword: String) {
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnSuccessListener {
                _updateState.value = "Password updated"
            }
            ?.addOnFailureListener {
                _updateState.value = it.message
            }
    }
}
