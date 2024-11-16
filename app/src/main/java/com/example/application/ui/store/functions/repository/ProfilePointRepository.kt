package com.example.application.ui.store.functions.repository

import com.example.application.RetrofitInstance.profilePointService
import com.example.application.ui.store.functions.data.BuyResponse
import com.example.application.ui.store.functions.service.ProfilePointService
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