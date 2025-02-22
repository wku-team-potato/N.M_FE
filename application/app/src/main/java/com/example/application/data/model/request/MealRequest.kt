package com.example.application.data.model.request

data class MealUpdateRequest(
    val food_id: Int,
    val meal_type: String,
    val serving_size: Int,
    val date: String
)

data class MealAddRequest(
    val food_id:Int,
    val serving_size: Int,
    val meal_type: String,
    val date : String
)