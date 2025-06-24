package com.priyanshparekh.fairshare.group.managegroup

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.util.ArrayList

class ManageGroupViewModel : ViewModel() {

    private val _searchStatus = MutableLiveData<Status<List<User>>>()
    val searchStatus: LiveData<Status<List<User>>> = _searchStatus

    fun getUserSearchResults(query: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.searchUser(query)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _searchStatus.value = Status.SUCCESS(body)
                } else {
                    _searchStatus.value = Status.ERROR("Error search users")
                }
            } else {
                _searchStatus.value = Status.ERROR("Error search users")
            }
        }
    }

    private val _addMembersStatus = MutableLiveData<Status<String>>()
    val addMembersStatus: LiveData<Status<String>> = _addMembersStatus

    fun addUsersToGroup(groupId: Long, newMembersList: ArrayList<User>) {
        _addMembersStatus.value = Status.LOADING()
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.addUsersToGroup(groupId, newMembersList)

            if (response.isSuccessful) {
                if (response.body() != null) {
                    _addMembersStatus.value = Status.SUCCESS("Members added in group successfully")
                } else {
                    _addMembersStatus.value = Status.ERROR("Error adding members in group")
                }
            } else {
                _addMembersStatus.value = Status.ERROR("Error adding members in group")
            }
        }
    }


    private val _exportDataStatus = MutableLiveData<Status<Pair<ResponseBody, String>>>()
    val exportDataStatus: LiveData<Status<Pair<ResponseBody, String>>> = _exportDataStatus

    fun exportData(type: String, groupId: Long) {
        _exportDataStatus.value = Status.LOADING()

        viewModelScope.launch {
            val response = if (type == "PDF") {
                RetrofitInstance.apiService.exportPdf(groupId)
            } else {
                RetrofitInstance.apiService.exportExcel(groupId)
            }

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _exportDataStatus.value = Status.SUCCESS(Pair(body, type))
                } else {
                    _exportDataStatus.value = Status.ERROR("Error exporting data")
                }
            } else {
                _exportDataStatus.value = Status.ERROR("Error exporting data")
            }
        }
    }


    private val _downloadStatus = MutableLiveData<Status<String>>()
    val downloadStatus: LiveData<Status<String>> = _downloadStatus

    fun downloadFile(pair: Pair<ResponseBody, String>) {
        _downloadStatus.value = Status.LOADING()
        viewModelScope.launch(Dispatchers.IO) {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val appFolder = File(downloadsFolder, "FairShare")

            if (!appFolder.exists()) {
                appFolder.mkdirs()
            }

            val type = pair.second
            val file = if (type == "PDF") {
                File(appFolder, "data.pdf")
            } else {
                File(appFolder, "data.xlsx")
            }

            val data = pair.first
            data.byteStream().use { input ->
                file.outputStream().use { output ->
                    _downloadStatus.postValue(Status.SUCCESS("File saved!"))
                    input.copyTo(output)
                }
            }
        }
    }
}