package com.example.application

import android.content.Context
import android.content.SharedPreferences

class SessionManager (context: Context){

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object{
        private const val CSRF_TOKEN_KEY = "csrf_token"
        private const val SESSION_ID_KEY = "session_id"
    }

    // CSRF 토큰 저장
    fun saveCsrfToken(csrfToken: String?) {
        sharedPreferences.edit().apply {
            putString(CSRF_TOKEN_KEY, csrfToken)
            apply()
        }
    }

    fun getCsrfToken(): String? {
        return sharedPreferences.getString(CSRF_TOKEN_KEY, null)
    }

    fun saveSessionId(sessionId: String?) {
        sharedPreferences.edit().apply {
            putString(SESSION_ID_KEY, sessionId)
                .apply()
        }
    }

    fun getSessionId(): String? {
        return sharedPreferences.getString(SESSION_ID_KEY, null)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}