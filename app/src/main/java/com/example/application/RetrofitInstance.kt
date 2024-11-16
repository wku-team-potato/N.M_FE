package com.example.application

import com.example.application.ui.auth.functions.service.FirstPersonalInfoService
import com.example.application.ui.auth.functions.service.PersonalInfoService
import com.example.application.ui.auth.functions.service.SignInService
import com.example.application.ui.auth.functions.service.SignUpService
import com.example.application.ui.store.functions.service.ProfilePointService
import com.example.application.ui.store.functions.service.StoreService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val sessionManager: SessionManager = MyApplication.getSessionManager()

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sessionManager))  // AuthInterceptor가 자동으로 토큰, 세션id를 추가
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val signUpService: SignUpService by lazy {
        retrofit.create(SignUpService::class.java)
    }

    val signInService: SignInService by lazy {
        retrofit.create(SignInService::class.java)
    }

    val firstPersonalInfoService : FirstPersonalInfoService by lazy{
        retrofit.create(FirstPersonalInfoService::class.java)
    }

    val personalInfoService : PersonalInfoService by lazy{
        retrofit.create(PersonalInfoService::class.java)
    }

    val storeService : StoreService by lazy{
        retrofit.create(StoreService::class.java)
    }

    val profilePointService : ProfilePointService by lazy{
        retrofit.create(ProfilePointService::class.java)
    }
}