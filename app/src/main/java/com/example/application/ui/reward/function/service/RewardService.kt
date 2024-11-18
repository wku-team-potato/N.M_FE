package com.example.application.ui.reward.function.service

import com.example.application.Config
import com.example.application.ui.reward.function.data.RewardResponse
import retrofit2.http.GET

interface RewardService {
    @GET(Config.REWARD_ENDPOINT)
    suspend fun getRewards():List<RewardResponse>
}