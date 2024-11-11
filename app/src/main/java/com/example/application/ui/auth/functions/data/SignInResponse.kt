package com.example.application.ui.auth.functions.data

data class SignInResponse (
    val success: String,
    val message: String?,
    val csrfToken: String?
)