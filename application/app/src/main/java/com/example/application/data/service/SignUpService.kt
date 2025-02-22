package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.request.SignUpRequest
import com.example.application.data.model.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService{
    @POST(Config.SIGNUP_ENDPOINT)
    suspend fun signupUser(
        @Body SignUpRequest: SignUpRequest
    ): Response<SignUpResponse>
}