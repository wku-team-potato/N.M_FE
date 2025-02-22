package com.example.application.ui.meals.function.service

import com.example.application.ui.meals.function.data.HealthResponse
import com.example.application.utils.Config
import retrofit2.http.GET

interface HealthService {
    @GET(Config.Profile_ENDPOINT)
    suspend fun getHeightWeightList(): List<HealthResponse>
}