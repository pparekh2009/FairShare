package com.priyanshparekh.fairshare.group.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.model.Expense
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ExpenseViewModel : ViewModel() {

    private val _addExpenseStatus = MutableLiveData<Status<Expense>>()
    val addExpenseStatus: LiveData<Status<Expense>> = _addExpenseStatus

    fun addExpense(groupId: Long, expense: Expense) {
        _addExpenseStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.addExpense(groupId, expense)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _addExpenseStatus.value = Status.SUCCESS(body)
                } else {
                    _addExpenseStatus.value = Status.ERROR("Error adding expense")
                }
            } else {
                _addExpenseStatus.value = Status.ERROR("Error adding expense")
            }
        }
    }


    private val _expensesStatus = MutableLiveData<Status<List<Expense>>>()
    val expensesStatus: LiveData<Status<List<Expense>>> = _expensesStatus

    fun getExpenses(groupId: Long) {
        _expensesStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getGroupExpenses(groupId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _expensesStatus.value = Status.SUCCESS(body)
                } else {
                    _addExpenseStatus.value = Status.ERROR("Error getting expenses")
                }
            } else {
                _addExpenseStatus.value = Status.ERROR("Error getting expenses")
            }
        }
    }

    fun uploadReceipt(groupId: Long, prepareFilePart: MultipartBody.Part, expense: Expense) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.uploadFile(prepareFilePart)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.success) {
                    expense.receiptUrl = body.message
                    addExpense(groupId, expense)
                }
            }
        }
    }

}