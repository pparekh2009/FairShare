package com.priyanshparekh.fairshare.group.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _dashboardDataStatus = MutableLiveData<Status<DashboardDTO>>()
    val dashboardDataStatus: LiveData<Status<DashboardDTO>> = _dashboardDataStatus

    fun getDashboardExpenses(groupId: Long) {
        _dashboardDataStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getDashboardStats(groupId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _dashboardDataStatus.value = Status.SUCCESS(body)
                } else {
                    _dashboardDataStatus.value = Status.ERROR("Error getting stats")
                }
            } else {
                _dashboardDataStatus.value = Status.ERROR("Error getting stats")
            }
        }
    }
}
