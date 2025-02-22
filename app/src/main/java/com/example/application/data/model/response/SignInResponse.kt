package com.example.application.data.model.response

data class SignInResponse (
    val success: String,
    val message: String?,
    val csrfToken: String?,
    val sessionId : String?
)