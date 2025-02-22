package com.example.application.utils

import android.util.Log
import com.example.application.data.service.DetectionService
import com.example.application.data.service.FirstPersonalInfoService
import com.example.application.data.service.GroupMyService
import com.example.application.data.service.LeaderBoardService
import com.example.application.data.service.LogoutService
import com.example.application.data.service.PersonalInfoService
import com.example.application.data.service.ProfileService
import com.example.application.data.service.RewardService
import com.example.application.data.service.ProfilePointService
import com.example.application.data.service.SignInService
import com.example.application.data.service.SignUpService
import com.example.application.data.service.StoreService
import com.example.application.ui.meals.function.service.FoodService
import com.example.application.ui.meals.function.service.HealthService
import com.example.application.ui.meals.function.service.MealService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

    private val timingInterceptor = Interceptor { chain ->
        val request = chain.request()

        // 시작 시간 기록
        val startTime = System.nanoTime()

        val response = chain.proceed(request)

        // 응답 시간 기록
        val endTime = System.nanoTime()
        val durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)

        // 요청 URL 및 소요 시간 로그 출력
        Log.d("API_DEBUG", "Request URL: ${request.url}")
        Log.d("API_DEBUG", "Request Duration: ${durationMs}ms")

        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val aiClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(timingInterceptor)
        .build()

    private val aiRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Config.AI_BASE_URL)
            .client(aiClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val detectionService: DetectionService by lazy {
        aiRetrofit.create(DetectionService::class.java)
    }

    val signUpService: SignUpService by lazy {
        retrofit.create(SignUpService::class.java)
    }

    val signInService: SignInService by lazy {
        retrofit.create(SignInService::class.java)
    }

    val firstPersonalInfoService: FirstPersonalInfoService by lazy {
        retrofit.create(FirstPersonalInfoService::class.java)
    }

    val personalInfoService: PersonalInfoService by lazy {
        retrofit.create(PersonalInfoService::class.java)
    }

    val storeService: StoreService by lazy {
        retrofit.create(StoreService::class.java)
    }

    val profilePointService: ProfilePointService by lazy {
        retrofit.create(ProfilePointService::class.java)
    }

    val profileService: ProfileService by lazy {
        retrofit.create(ProfileService::class.java)
    }

    val rewardService: RewardService by lazy {
        retrofit.create(RewardService::class.java)
    }

    val leaderBoardService: LeaderBoardService by lazy {
        retrofit.create(LeaderBoardService::class.java)
    }

    val mealService: MealService by lazy {
        retrofit.create(MealService::class.java)
    }

    val healthService: HealthService by lazy {
        retrofit.create(HealthService::class.java)
    }

    val foodService: FoodService by lazy {
        retrofit.create(FoodService::class.java)
    }

    val logoutService: LogoutService by lazy {
        retrofit.create(LogoutService::class.java)
    }

    val groupMyService: GroupMyService by lazy {
        retrofit.create(GroupMyService::class.java)
    }
}
