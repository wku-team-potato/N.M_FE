package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.response.FirstPersonalInfoResponse
import retrofit2.Response
import retrofit2.http.GET

interface FirstPersonalInfoService {
    @GET(Config.PersonalInfo_ENDPOINT)
    suspend fun getFirstPersonalInfoUser(): Response<List<FirstPersonalInfoResponse>>
}