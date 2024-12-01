package com.example.application.utils

import com.example.application.data.service.FirstPersonalInfoService
import com.example.application.data.service.LeaderBoardService
import com.example.application.data.service.PersonalInfoService
import com.example.application.data.service.ProfileService
import com.example.application.data.service.RewardService
import com.example.application.data.service.ProfilePointService
import com.example.application.data.service.SignInService
import com.example.application.data.service.SignUpService
import com.example.application.data.service.StoreService
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

    val profileService : ProfileService by lazy{
        retrofit.create(ProfileService::class.java)
    }

    val rewardService : RewardService by lazy{
        retrofit.create(RewardService::class.java)
    }

    val leaderBoardService : LeaderBoardService by lazy{
        retrofit.create(LeaderBoardService::class.java)
    }
}