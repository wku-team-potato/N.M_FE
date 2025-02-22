package com.example.application.ui.meals.function.service


import com.example.application.ui.meals.function.data.FoodResponse
import com.example.application.utils.Config
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodService {

    @GET(Config.MealSearch_ENDPOINT)
    suspend fun searchFood(@Path("food_name") foodName : String) : List<FoodResponse>

    @GET(Config.MEALSEARCH_ID_ENDPOINT)
    suspend fun searchFoodById(@Path("food_id") foodId : Int) : List<FoodResponse>
}