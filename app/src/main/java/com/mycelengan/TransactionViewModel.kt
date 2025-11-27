package com.mycelengan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycelengan.pages.TransactionItem

class TransactionViewModel : ViewModel() {

    private val repo = TransactionRepository()

    private val _transactions = MutableLiveData<List<TransactionItem>>()
    val transactions: LiveData<List<TransactionItem>> = _transactions

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    init {
        repo.listenTransactions {
            _transactions.value = it
        }
    }

    fun addTransaction(
        type: String,
        amount: Int,
        desc: String,
        date: String,
        iconName: String
    ) {
        _loading.value = true

        repo.addTransaction(
            type = type,
            amount = amount,
            desc = desc,
            date = date,
            iconName = iconName,
            onSuccess = {
                _loading.value = false
            },
            onFail = {
                _loading.value = false
            }
        )
    }
}
