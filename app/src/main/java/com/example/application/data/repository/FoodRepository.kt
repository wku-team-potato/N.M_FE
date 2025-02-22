package com.example.application.data.repository

import com.example.application.ui.meals.function.data.FoodResponse
import com.example.application.ui.meals.function.service.FoodService
import com.example.application.utils.Config
import retrofit2.http.GET

class FoodRepository(private val api: FoodService) {
    @GET(Config.MealSearch_ENDPOINT)
    suspend fun searchFoodList(foodName: String): List<FoodResponse> {
        return api.searchFood(foodName)
    }

    @GET(Config.MEALSEARCH_ID_ENDPOINT)
    suspend fun searchFoodById(foodId: Int): FoodResponse? {
        return api.searchFoodById(foodId).firstOrNull()
    }
}