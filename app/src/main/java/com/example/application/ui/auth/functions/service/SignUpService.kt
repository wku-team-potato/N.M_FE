package com.example.application.ui.auth.functions.service

import com.example.application.Config
import com.example.application.ui.auth.functions.data.SignUpRequest
import com.example.application.ui.auth.functions.data.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService{
    @POST(Config.SIGNUP_ENDPOINT)
    suspend fun signupUser(
        @Body SignUpRequest: SignUpRequest
    ): Response<SignUpResponse>
}