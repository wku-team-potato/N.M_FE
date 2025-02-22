package com.example.application.data.service

import com.example.application.data.model.response.LogoutResponse
import com.example.application.utils.Config
import retrofit2.Response
import retrofit2.http.POST

interface LogoutService {
    @POST(Config.LOGOUT)
    suspend fun logoutUser() : LogoutResponse
}