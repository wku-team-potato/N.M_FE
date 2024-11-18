package com.example.application.ui.reward.function.repository

import com.example.application.ui.reward.function.data.RewardResponse
import com.example.application.ui.reward.function.service.RewardService

class RewardRepository(private val service: RewardService) {
    suspend fun getRewards(): List<RewardResponse> {
        return service.getRewards()
    }
}