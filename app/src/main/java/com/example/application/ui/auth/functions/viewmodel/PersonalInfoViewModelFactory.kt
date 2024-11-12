package com.example.application.ui.auth.functions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.ui.auth.functions.repository.PersonalInfoRepository

class PersonalInfoViewModelFactory(
    private val personalInfoRepository: PersonalInfoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalInfoViewModel::class.java)) {
            return PersonalInfoViewModel(personalInfoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}