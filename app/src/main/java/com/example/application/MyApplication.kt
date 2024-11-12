// MyApplication.kt
package com.example.application

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    // Application 전역에서 사용할 SessionManager 인스턴스를 선언
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()

        // Application Context를 사용하여 SessionManager 초기화
        sessionManager = SessionManager(applicationContext)
    }

    // MyApplication의 context를 외부에서도 접근할 수 있도록 메서드 제공
    companion object {
        private lateinit var instance: MyApplication

        fun getSessionManager(): SessionManager {
            return instance.sessionManager
        }
    }

    init {
        instance = this
    }
}