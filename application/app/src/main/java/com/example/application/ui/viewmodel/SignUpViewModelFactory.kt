package com.example.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.SignUpRepository
import com.example.application.functions.viewmodel.SignUpViewModel

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