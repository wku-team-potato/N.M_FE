package com.example.application.functions.viewmodel

import androidx.lifecycle.ViewModel
import com.example.application.data.model.response.PersonalInfoResponse
import com.example.application.data.repository.PersonalInfoRepository
import retrofit2.Response

class PersonalInfoViewModel(
    private val personalInfoRepository: PersonalInfoRepository
) : ViewModel() {

    suspend fun submitPersonalInfo(height: Int, weight: Int): Response<PersonalInfoResponse> {
        return personalInfoRepository.updatePersonalInfo(height, weight)
    }
}