package com.example.application.ui.auth.functions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.ui.auth.functions.repository.SignInRepository

class SignInViewModelFactory(
    private val signInRepository: SignInRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(signInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
