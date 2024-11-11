package com.example.application

import android.content.Context
import android.content.SharedPreferences

class SessionManger (context: Context){

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // CSRF 토큰 저장
    fun saveCsrfToken(csrfToken: String?) {
        sharedPreferences.edit().apply {
            putString("csrfToken", csrfToken)
            apply()
        }
    }

    fun getCsrfToken(): String? {
        return sharedPreferences.getString("csrfToken", null)
    }

    // 로그아웃 시 세션 데이터 삭제
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}