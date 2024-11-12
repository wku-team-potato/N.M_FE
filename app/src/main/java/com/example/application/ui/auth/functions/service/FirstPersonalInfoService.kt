package com.example.application.ui.auth.functions.service

import com.example.application.Config
import com.example.application.ui.auth.functions.data.FirstPersonalInfoResponse
import retrofit2.Response
import retrofit2.http.GET

interface FirstPersonalInfoService {
    @GET(Config.PersonalInfo_ENDPOINT)
    suspend fun getFirstPersonalInfoUser(): Response<List<FirstPersonalInfoResponse>>
}