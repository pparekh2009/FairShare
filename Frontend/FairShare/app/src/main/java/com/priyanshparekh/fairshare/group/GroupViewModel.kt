package com.priyanshparekh.fairshare.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {

    private val _groupMembersStatus = MutableLiveData<Status<List<User>>>()
    val groupMembersStatus: LiveData<Status<List<User>>> = _groupMembersStatus

    fun getGroupMembers(groupId: Long) {
        _groupMembersStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getGroupMembers(groupId)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _groupMembersStatus.postValue(Status.SUCCESS(body))
                } else {
                    _groupMembersStatus.value = Status.ERROR("Error getting group members")
                }
            } else {
                _groupMembersStatus.value = Status.ERROR("Error getting group members")
            }
        }
    }

}