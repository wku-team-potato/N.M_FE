package com.example.application

import com.example.application.ui.auth.functions.service.SignInService
import com.example.application.ui.auth.functions.service.SignUpService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val signUpService: SignUpService by lazy {
        retrofit.create(SignUpService::class.java)
    }

    val signInService: SignInService by lazy {
        retrofit.create(SignInService::class.java)
    }
}