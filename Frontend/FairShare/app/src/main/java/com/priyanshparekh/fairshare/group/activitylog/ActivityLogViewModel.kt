package com.priyanshparekh.fairshare.group.activitylog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.model.ActivityLog
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch

class ActivityLogViewModel : ViewModel() {

    private val _activityLogs = MutableLiveData<Status<List<ActivityLog>>>()
    val activityLogs: LiveData<Status<List<ActivityLog>>> = _activityLogs

    fun getActivityLogs(groupId: Long) {
        _activityLogs.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getActivityLogs(groupId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _activityLogs.value = Status.SUCCESS(body)
                } else {
                    _activityLogs.value = Status.ERROR("Error getting logs")
                }
            } else {
                _activityLogs.value = Status.ERROR("Error getting logs")
            }
        }
    }

    private val _addActivityLogStatus = MutableLiveData<Status<ActivityLog>>()

    fun addActivityLog(groupId: Long, activityLog: ActivityLog) {
        _addActivityLogStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.addActivityLog(groupId, activityLog)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _addActivityLogStatus.value = Status.SUCCESS(body)
                } else {
                    _addActivityLogStatus.value = Status.ERROR("Error adding activity log")
                }
            } else {
                _addActivityLogStatus.value = Status.ERROR("Error adding activity log")
            }
        }
    }
}