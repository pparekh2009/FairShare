package com.priyanshparekh.fairshare.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.model.Group
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var _groupListStatus = MutableLiveData<Status<List<Group>>>()
    var groupListStatus: LiveData<Status<List<Group>>> = _groupListStatus

    fun getGroupList(userId: Long) {
        _groupListStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getGroups(userId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _groupListStatus.value = Status.SUCCESS(body)
                } else {
                    _groupListStatus.value = Status.ERROR("Error getting group list")
                }
            } else {
                _groupListStatus.value = Status.ERROR("Error getting group list")
            }
        }
    }

    private val _addGroupStatus = MutableLiveData<Status<Group>>()
    val addGroupStatus: LiveData<Status<Group>> = _addGroupStatus

    fun addGroup(userId: Long, group: Group) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.addGroup(userId, group)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _addGroupStatus.value = Status.SUCCESS(body)
                } else {
                    _addGroupStatus.value = Status.ERROR("Error adding group")
                }
            } else {
                _addGroupStatus.value = Status.ERROR("Error adding group")
            }
        }
    }

}