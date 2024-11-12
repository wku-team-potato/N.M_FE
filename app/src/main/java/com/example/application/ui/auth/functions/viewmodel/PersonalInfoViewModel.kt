package com.example.application.ui.auth.functions.viewmodel

import androidx.lifecycle.ViewModel
import com.example.application.ui.auth.functions.repository.PersonalInfoRepository
import com.example.application.ui.auth.functions.data.PersonalInfoResponse
import retrofit2.Response

class PersonalInfoViewModel(
    private val personalInfoRepository: PersonalInfoRepository
) : ViewModel() {

    suspend fun submitPersonalInfo(height: Int, weight: Int): Response<PersonalInfoResponse> {
        return personalInfoRepository.updatePersonalInfo(height, weight)
    }
}