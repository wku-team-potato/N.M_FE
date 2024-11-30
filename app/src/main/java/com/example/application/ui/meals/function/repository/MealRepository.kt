package com.example.application.ui.meals.function.repository

import android.util.Log
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.data.MealSummaryResponse
import com.example.application.ui.meals.function.service.MealService

class MealRepository(private val service: MealService) {
    suspend fun getMealSummary(date: String): MealSummaryResponse {
        val response = service.getMealSummary(date)
        Log.d("MealRepository", "Requesting meal summary for date: $date")
        Log.d("MealRepository", "API response: $response")
        return response
    }

    suspend fun getMealList(date: String): List<MealResponse> {
        val response = service.getMealList(date)
        Log.d("MealRepository", "Meal List API Response: $response")
        return response
    }
}