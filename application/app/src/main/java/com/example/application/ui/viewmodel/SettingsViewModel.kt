package com.example.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.LogoutResponse
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.repository.FirstPersonalInfoRepository
import com.example.application.data.repository.LogoutRepository
import com.example.application.data.repository.ProfilePointRepository
import com.example.application.data.repository.ProfileRepository
import com.example.application.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sessionManager: SessionManager,
    private val logoutRepository: LogoutRepository,
    private val profileRepository: ProfileRepository,
    private val personalInfoRepository: FirstPersonalInfoRepository
) : ViewModel() {
    private val _logoutResult = MutableLiveData<LogoutResponse>()
    val logoutResult get() = _logoutResult

    private val _userName = MutableLiveData<String>()
    val userName get() = _userName

    private val _userHeight = MutableLiveData<Float>()
    val userHeight get() = _userHeight

    private val _userWeight = MutableLiveData<Float>()
    val userWeight get() = _userWeight

    private val _userPoint = MutableLiveData<Int>()
    val userPoint get() = _userPoint

    private val _updateResult = MutableLiveData<Boolean?>()
    val updateResult get() = _updateResult

    fun updateProfileInfo(updatedProfile: ProfileResponse) {
        viewModelScope.launch {
            val response = profileRepository.updateProfileInfo(updatedProfile)

            Log.d("SettingsViewModel", "updateProfileInfo called")

            if (response != null) {
                _userName.value = response.username
                _userHeight.value = response.height
                _userWeight.value = response.weight
                _updateResult.value = true

                Log.d("SettingsViewModel", "API Response: $response")
            } else {
                _updateResult.value = false
                Log.d("SettingsViewModel", "API Error: $response")
            }

            _updateResult.value = null
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            val user_id = personalInfoRepository.getFirstInfo()?.user_id?.toInt()
            val profile = user_id?.let { profileRepository.getProfileInfoById(it) }

            _userName.value = profile?.username
            _userHeight.value = profile?.height
            _userWeight.value = profile?.weight
            _userPoint.value = profile?.total_points
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            _logoutResult.value = logoutRepository.logoutUser()
        }
    }
}