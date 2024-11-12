package com.example.application.ui.auth.functions.repository

import android.util.Log
import com.example.application.ui.auth.functions.data.PersonalInfoRequest
import com.example.application.ui.auth.functions.data.PersonalInfoResponse
import com.example.application.ui.auth.functions.service.PersonalInfoService
import retrofit2.Response

class PersonalInfoRepository(private val personalInfoService: PersonalInfoService) {

    suspend fun updatePersonalInfo(height: Int, weight: Int): Response<PersonalInfoResponse> {
        val request = PersonalInfoRequest(height, weight)
        val response = personalInfoService.personalInfoUser(request)

        if (!response.isSuccessful) {
            Log.e("PersonalInfoRepository", "Failed to update info. Code: ${response.code()}, Error: ${response.errorBody()?.string()}")
        }

        return response
    }
}