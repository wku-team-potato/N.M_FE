package com.example.application.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.LogoutResponse
import com.example.application.data.repository.LogoutRepository
import com.example.application.utils.SessionManager
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sessionManager: SessionManager,
    private val logoutRepository: LogoutRepository
) : ViewModel() {
    private val _logoutResult = MutableLiveData<LogoutResponse>()
    val logoutResult get() = _logoutResult



    fun logoutUser() {
        viewModelScope.launch {
            _logoutResult.value = logoutRepository.logoutUser()
        }
    }
}