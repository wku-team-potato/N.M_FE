package com.example.application.data.service

import com.example.application.utils.Config
import com.example.application.data.model.response.RewardResponse
import retrofit2.http.GET

interface RewardService {
    @GET(Config.REWARD_ENDPOINT)
    suspend fun getRewards():List<RewardResponse>
}