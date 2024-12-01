package com.example.application.data.repository

import com.example.application.data.model.response.RewardResponse
import com.example.application.data.service.RewardService

class RewardRepository(private val service: RewardService) {
    suspend fun getRewards(): List<RewardResponse> {
        return service.getRewards()
    }
}