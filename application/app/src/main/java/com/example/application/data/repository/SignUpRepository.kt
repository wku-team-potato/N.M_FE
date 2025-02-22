package com.example.application.data.repository

import com.example.application.data.model.request.SignUpRequest
import com.example.application.data.model.response.SignUpResponse
import com.example.application.data.service.SignUpService

class SignUpRepository (private val signUpService: SignUpService) {

    suspend fun signUpUser(username: String, password: String, nickname: String): SignUpResponse? {
        val request =
            SignUpRequest(username, password, nickname)

        val response = signUpService.signupUser(request)

        return if (response.isSuccessful){
            response.body()
        } else{
            null
        }
    }

}