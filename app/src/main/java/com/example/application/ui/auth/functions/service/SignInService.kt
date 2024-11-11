package com.example.application.ui.auth.functions.service

import com.example.application.Config
import com.example.application.ui.auth.functions.data.SignInRequest
import com.example.application.ui.auth.functions.data.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInService {
    @POST(Config.SIGNIN_ENDPOINT)
    suspend fun signInUser(
        @Body SignInRequest: SignInRequest
    ): Response<SignInResponse>
}