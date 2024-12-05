package com.example.application.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.LogoutRepository
import com.example.application.utils.SessionManager

class SettingsViewModelFactory(
    private val sessionManager: SessionManager,
    private val logoutRepository: LogoutRepository
) : ViewModelProvider.Factory {

    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(sessionManager, logoutRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}