package com.example.application.ui.profile.function.repository

import android.util.Log
import com.example.application.ui.profile.function.data.ProfileResponse
import com.example.application.ui.profile.function.service.ProfileService

class ProfileRepository(private val service : ProfileService) {
    suspend fun getProfileInfo(): ProfileResponse? {
        Log.d("ProfileRepository", "getProfileInfo called")
        val response = service.getProfile()
        if (response.isSuccessful) {
            Log.d("ProfileRepository", "API Response: ${response.body()}")
            return response.body()?.firstOrNull()
        } else {
            Log.e("ProfileRepository", "API Error: ${response.code()}")
            return null
        }
    }

    suspend fun updateProfileInfo(updatedData: ProfileResponse): ProfileResponse? {
        val response = service.updateProfileInfo(updatedData)
        return if (response.isSuccessful) response.body() else null
    }
}