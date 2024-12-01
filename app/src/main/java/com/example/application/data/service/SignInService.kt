package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.request.SignInRequest
import com.example.application.data.model.response.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInService {
    @POST(Config.SIGNIN_ENDPOINT)
    suspend fun signInUser(
        @Body SignInRequest: SignInRequest
    ): Response<SignInResponse>
}