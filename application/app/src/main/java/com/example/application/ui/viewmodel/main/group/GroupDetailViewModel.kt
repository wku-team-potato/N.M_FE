package com.example.application.ui.viewmodel.main.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.groups.request.GroupPublicRequest
import com.example.application.data.model.groups.response.GroupDetailResponse
import com.example.application.data.model.groups.response.GroupMemberResponse
import com.example.application.data.model.groups.response.GroupResponse
import com.example.application.data.repository.GroupMyRepository
import com.example.application.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val groupRepository: GroupMyRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _groupInfo = MutableLiveData<GroupResponse?>()
    val groupInfo: MutableLiveData<GroupResponse?> = _groupInfo

    private val _memberList = MutableLiveData<List<GroupMemberResponse>?>()
    val memberList: MutableLiveData<List<GroupMemberResponse>?> = _memberList

    private val _myInfo = MutableLiveData<GroupMemberResponse?>()
    val myInfo: MutableLiveData<GroupMemberResponse?> = _myInfo

    private val _isPublic = MutableLiveData<Boolean>()
    val isPublic: MutableLiveData<Boolean> = _isPublic

    private val _adapterData = MutableLiveData<MutableList<AdapterData>>()
    val adapterData: MutableLiveData<MutableList<AdapterData>> = _adapterData

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    fun getMemberCount(): Int {
        return memberList.value?.size ?: 0
    }

    fun loadGroupInfo(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = groupRepository.groupDetail(groupId)

                Log.d("GroupDetailViewModel", "Group info: $response")

                val group_ = GroupResponse(
                    id = response.group.id,
                    name = response.group.name,
                    description = response.group.description,
                    creator = response.group.creator,
                    created_at = response.group.created_at,
                    updated_at = response.group.updated_at
                )

                _groupInfo.value = group_
                _memberList.value = response.group.members

                loadAdapterData()
            } catch (e: Exception) {
                _groupInfo.value = null
                _memberList.value = null
                Log.d("GroupDetailViewModel", "Group info: $e")
            }
        }
    }

    fun loadAdapterData() {

        viewModelScope.launch {
            val list = mutableListOf<AdapterData>()
            memberList.value?.forEach {

                var profile_info = profileRepository.getProfileInfoById(it.user_id)

                if (profile_info != null) {
                    list.add(
                        AdapterData(
                            is_public = it.is_public,
                            username = profile_info.username + "님",
                            userHeight = profile_info.height.toDouble(),
                            userWeight = profile_info.weight.toDouble()
                        )
                    )
                }

//                if (it.is_public) {
//                    if (profile_info != null) {
//                        list.add(
//                            AdapterData(
//                                is_public = it.is_public,
//                                username = profile_info.username + "님",
//                                userHeight = profile_info.height.toDouble(),
//                                userWeight = profile_info.weight.toDouble()
//                            )
//                        )
//                    } else {
//                        list.add(
//                            AdapterData(
//                                is_public = it.is_public,
//                                username = profile_info.username + "님",
//                                userHeight = profile_info.height.toDouble(),
//                                userWeight = profile_info.weight.toDouble()
//                            )
//                        )
//                    }
//                } else {
//                    list.add(
//                        AdapterData(
//                            is_public = it.is_public,
//                            username = profile_info.username + "님",
//                            userHeight = profile_info.height.toDouble(),
//                            userWeight = profile_info.weight.toDouble()
//                        )
//                    )
//                }
            }
            _adapterData.value = list
            _adapterData.value = _adapterData.value
        }
    }

    fun loadMyGroupInfo(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = groupRepository.getMyGroupInfo(groupId)
                if (response.isNotEmpty()) {
                    val firstItem = response.first()
                    _isPublic.value = firstItem.is_public
                    _myInfo.value = firstItem
                } else {
                    Log.d("GroupDetailViewModel", "My info: Empty response")
                    _isPublic.value = false
                    _myInfo.value = null
                }
            } catch (e: Exception) {
                _isPublic.value = false
                _myInfo.value = null
                Log.d("GroupDetailViewModel", "My info: $e")
            }
        }
    }

    fun togglePublic(groupId: Int, isPublic: Boolean) {
        viewModelScope.launch {
            try {
                groupRepository.updateGroupPublic(
                    groupId,
                    GroupPublicRequest(isPublic)
                )

                loadMyGroupInfo(groupId)
                loadAdapterData()

                _isPublic.value = _myInfo.value?.is_public
            } catch (e: Exception) {
                Log.d("GroupDetailViewModel", "Toggle public: $e")
            }
        }
    }

}

data class AdapterData(
    val username: String,
    val userHeight: Double,
    val userWeight: Double,
    val is_public: Boolean
)