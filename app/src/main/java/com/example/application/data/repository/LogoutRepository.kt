package com.example.application.data.repository

import android.util.Log
import com.example.application.data.model.response.LogoutResponse
import com.example.application.data.service.LogoutService
import retrofit2.Response

class LogoutRepository(private val service: LogoutService) {
    suspend fun logoutUser() : LogoutResponse {
        Log.d("LogoutRepository", "Logging out user")
        val response = service.logoutUser()
        return response
    }
}