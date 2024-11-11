package com.example.application.ui.auth.functions.repository

import com.example.application.ui.auth.functions.data.SignInRequest
import com.example.application.ui.auth.functions.data.SignInResponse
import com.example.application.ui.auth.functions.service.SignInService

class SignInRepository (private val signInService: SignInService) {

    suspend fun signInUser(username: String, password: String): SignInResponse? {
        val request = SignInRequest(username, password)

        val response = signInService.signInUser(request)

        return if (response.isSuccessful) {
            val cookieHeader = response.headers()["Cookie"]
            val csrfToken = cookieHeader?.substringAfter("csrftoken=")?.substringBefore(";")

            response.body()?.copy(csrfToken = csrfToken)
        } else {
            null
        }
    }
}