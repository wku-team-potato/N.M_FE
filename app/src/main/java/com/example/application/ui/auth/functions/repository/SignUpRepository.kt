package com.example.application.ui.auth.functions.repository

import com.example.application.ui.auth.functions.data.SignUpRequest
import com.example.application.ui.auth.functions.data.SignUpResponse
import com.example.application.ui.auth.functions.service.SignUpService

class SignUpRepository (private val signUpService: SignUpService) {

    suspend fun signUpUser(username: String, password: String, nickname: String): SignUpResponse? {
        val request = SignUpRequest(username, password, nickname)

        val response = signUpService.signupUser(request)

        return if (response.isSuccessful){
            response.body()
        } else{
            null
        }
    }

}