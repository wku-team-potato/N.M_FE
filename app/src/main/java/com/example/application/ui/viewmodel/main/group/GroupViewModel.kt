package com.example.application.ui.viewmodel.main.group

import android.net.http.HttpException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.groups.request.GroupCreateRequest
import com.example.application.data.model.groups.request.GroupJoinRequest
import com.example.application.data.model.groups.response.GroupResponse
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.model.response.ProfileResponse_2
import com.example.application.data.repository.GroupMyRepository
import com.example.application.data.repository.ProfileRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class GroupViewModel(
    private val ProfileRepository: ProfileRepository,
    private val GroupMyRepository: GroupMyRepository
) : ViewModel() {

    private val _myInfo = MutableLiveData<ProfileResponse?>()
    val myInfo: MutableLiveData<ProfileResponse?> = _myInfo

    private val _myGroups = MutableLiveData<List<GroupWithProfile>>()
    val myGroups: MutableLiveData<List<GroupWithProfile>> = _myGroups

    private val _groups = MutableLiveData<List<GroupResponse>>()
    val groups: MutableLiveData<List<GroupResponse>> = _groups

    private val _groupsAll = MutableLiveData<List<GroupWithProfile>>()
    val groupsAll: MutableLiveData<List<GroupWithProfile>> = _groupsAll

    private val _isLoading  = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: MutableLiveData<String> = _errorMessage

    private val _isGroupCreated = MutableLiveData<Boolean>()
    val isGroupCreated: MutableLiveData<Boolean> = _isGroupCreated

    fun loadMyInfo() {
        viewModelScope.launch {
            try {
                val response = ProfileRepository.getProfileInfo()
                _myInfo.value = response
                Log.d("GroupViewModel", "My info: $response")
            } catch (e: Exception) {
                _myInfo.value = null
            }
        }
    }

    fun loadMyGroups() {
        if (_groups.value.isNullOrEmpty()) {
            _isLoading.value = true
        }
        viewModelScope.launch {
            try {
                val response = GroupMyRepository.getMyGroups()

                val updatedResponse = response.map { group ->
                    val profileResponse = ProfileRepository.getProfileInfoById(group.creator)
                    GroupWithProfile(group, profileResponse!!)
                }

                _myGroups.value = updatedResponse
                Log.d("GroupViewModel", "My groups: $response")
            } catch (e: Exception) {
                _myGroups.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllGroups() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = GroupMyRepository.getAllGroups()

                val updatedResponse = response.map { group ->
                    val profileResponse = ProfileRepository.getProfileInfoById(group.creator)
                    GroupWithProfile(group, profileResponse!!)
                }

                _groupsAll.value = updatedResponse
                Log.d("GroupViewModel", "All groups: $response")
            } catch (e: Exception) {
                _groupsAll.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchGroups(search: String) {
        if (_groups.value.isNullOrEmpty()) {
            _isLoading.value = true
        }
        viewModelScope.launch {
            try {
                val response = GroupMyRepository.getSearchGroups(search)

                Log.d("GroupViewModel", "Search groups: $response")

                val updatedResponse = response.map { group ->
                    val profileResponse = ProfileRepository.getProfileInfoById(group.creator)
                    GroupWithProfile(group, profileResponse!!)
                }

                _groupsAll.value = updatedResponse
                Log.d("GroupViewModel", "All groups: $response")
            } catch (e: Exception) {
                Log.d("GroupViewModel", "Error searching groups", e)
                _groupsAll.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    val _createGroupMessage = MutableLiveData<String>()
    val createGroupMessage: MutableLiveData<String> = _createGroupMessage

    fun createGroup(groupName: String, groupDescription: String) {
        val request = GroupCreateRequest(groupName, groupDescription)


        viewModelScope.launch {
            try {
                val response = GroupMyRepository.createGroup(request)

                _createGroupMessage.value = response.message
            } catch (e: Exception) {
                _createGroupMessage.value = "동일한 이름의 그룹이 이미 존재합니다."
            } finally {
                loadMyGroups()
            }
        }
    }

    fun joinGroup(is_public: Boolean, groupId: Int) {
        viewModelScope.launch {
            try {
                val response = GroupMyRepository.joinGroup(groupJoinRequest = GroupJoinRequest(is_public, groupId))
                Log.d("GroupViewModel", "Group joined: $response")
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error joining group", e)
            } finally {
                loadMyGroups()
                loadAllGroups()
            }
        }
    }

    fun leaveGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                GroupMyRepository.leaveGroup(groupId)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error leaving group", e)
            } finally {
                loadMyGroups()
            }
        }
    }

    data class GroupWithProfile(
        val group: GroupResponse,
        val profile: ProfileResponse_2
    )

}