package com.priyanshparekh.fairshare.group.balances

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.group.balances.notification.NotificationRequest
import com.priyanshparekh.fairshare.model.BalanceInfo
import com.priyanshparekh.fairshare.model.PayData
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch

class BalancesViewModel : ViewModel() {

    private val _balanceInfoStatus = MutableLiveData<Status<List<BalanceInfo>>>()
    val balanceInfoStatus: LiveData<Status<List<BalanceInfo>>> = _balanceInfoStatus

    fun getBalanceInfo(groupId: Long, userId: Long) {
        _balanceInfoStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getBalanceInfo(groupId, userId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _balanceInfoStatus.value = Status.SUCCESS(body)
                } else {
                    _balanceInfoStatus.value = Status.ERROR("Error getting balance info")
                }
            } else {
                _balanceInfoStatus.value = Status.ERROR("Error getting balance info")
            }
        }
    }

    private val _updateBalanceInfo = MutableLiveData<Status<List<BalanceInfo>>>()
    val updateBalanceInfo: LiveData<Status<List<BalanceInfo>>> = _updateBalanceInfo

    fun updateBalanceInfo(newOwesToList: List<BalanceInfo>, groupId: Long) {
        _updateBalanceInfo.value = Status.LOADING()

        viewModelScope.launch {

            val response = RetrofitInstance.apiService.updateBalanceInfo(newOwesToList, groupId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _updateBalanceInfo.value = Status.SUCCESS(body)
                } else {
                    _updateBalanceInfo.value = Status.ERROR("Error updating balance info")
                }
            } else {
                _updateBalanceInfo.value = Status.ERROR("Error updating balance info")
            }
        }
    }

    private val _payUserStatus = MutableLiveData<Status<PayData>>()
    val payUserStatus: LiveData<Status<PayData>> = _payUserStatus

    fun payUser(payData: PayData, groupId: Long) {
        _payUserStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.payUser(payData, groupId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _payUserStatus.value = Status.SUCCESS(body)
                } else {
                    _payUserStatus.value = Status.ERROR("Error making payment")
                }
            } else {
                _payUserStatus.value = Status.ERROR("Error making payment")
            }
        }
    }

    fun notifyUsers(notificationRequest: NotificationRequest) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.sendNotification(notificationRequest)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Log.d("TAG", "notifyUsers: balancesViewModel: $body")
                } else {
                    Log.d("TAG", "notifyUsers: balancesViewModel: inner if: code: ${response.code()}")
                    Log.d("TAG", "notifyUsers: balancesViewModel: inner if: message: ${response.message()}")
                }
            } else {
                Log.d("TAG", "notifyUsers: balancesViewModel: outer if: code: ${response.code()}")
                Log.d("TAG", "notifyUsers: balancesViewModel: outer if: message: ${response.message()}")
            }
        }
    }
}