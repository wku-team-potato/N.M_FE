package com.example.application.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url().toString()

        if (url.contains("login") || url.contains("signup")){
            return chain.proceed(request)
        }

        val csrfToken = sessionManager.getCsrfToken()
        val sessionId = sessionManager.getSessionId()

        Log.d("AuthInterceptor", "Adding headers - CSRF Token: $csrfToken, Session ID: $sessionId")

        val newRequest = if (csrfToken != null && sessionId != null){
            request.newBuilder()
                .addHeader("Cookie", "csrftoken=$csrfToken; sessionid=$sessionId")
                .addHeader("X-CSRFToken", csrfToken)
                .build()
        } else {
            Log.e("AuthInterceptor", "CSRF 토큰이나 세션 ID가 null 입니다.")
            request
        }

        return chain.proceed(newRequest)
    }
}