package com.example.application.data.repository

import com.example.application.utils.RetrofitInstance.profilePointService
import com.example.application.data.model.response.BuyResponse
import com.example.application.data.service.ProfilePointService
import retrofit2.Response

class ProfilePointRepository(private val service: ProfilePointService) {
    suspend fun getTotalPoints(): Int {
        val response = service.getTotalPoints()
        if (response.isSuccessful) {
            return response.body()?.total_points ?: 0
        } else {
            throw Exception("Failed to fetch total points: ${response.errorBody()?.string()}")
        }
    }

    suspend fun buyItem(itemId: Int): Response<BuyResponse> {
        return profilePointService.buyItem(itemId)
    }
}