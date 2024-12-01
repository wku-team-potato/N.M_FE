package com.example.application.data.repository

import android.util.Log
import com.example.application.data.model.request.SignInRequest
import com.example.application.data.model.response.SignInResponse
import com.example.application.data.service.SignInService


class SignInRepository (private val signInService: SignInService) {

    suspend fun signInUser(username: String, password: String): SignInResponse? {
        val request = SignInRequest(username, password)

        val response = signInService.signInUser(request)

        return if (response.isSuccessful) {
            val csrfToken = response.headers().values("Set-Cookie")
                .find { it.startsWith("csrftoken=") }
                ?.split(";")
                ?.find { it.trim().startsWith("csrftoken=") }
                ?.substringAfter("csrftoken=")

            val sessionId = response.headers().values("Set-Cookie")
                .find { it.startsWith("sessionid=") }
                ?.split(";")
                ?.firstOrNull { it.trim().startsWith("sessionid=") }
                ?.substringAfter("sessionid=")

            Log.d("csrf", "CSRF Token: $csrfToken")
            Log.d("세션ID", "Session ID: $sessionId")

            response.body()?.copy(csrfToken = csrfToken, sessionId = sessionId)

        } else {
            null
        }
    }
}