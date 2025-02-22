package com.example.application.functions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.SignInRepository
import com.example.application.utils.SessionManager

class SignInViewModelFactory(
    private val signInRepository: SignInRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(sessionManager, signInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
