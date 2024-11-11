package com.example.application.ui.auth.functions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.ui.auth.functions.data.SignUpResponse
import com.example.application.ui.auth.functions.repository.SignUpRepository

class SignUpViewModelFactory(
    private val signUpRepository: SignUpRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(signUpRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}