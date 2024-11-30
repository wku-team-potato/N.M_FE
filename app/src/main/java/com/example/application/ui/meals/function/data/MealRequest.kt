package com.example.application.ui.meals.function.data

data class MealUpdateRequest(
    val food_id: Int,
    val meal_type: String,
    val serving_size: Int,
    val date: String
)
