package com.example.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.DetectionRepository
import com.example.application.data.repository.FoodRepository
import com.example.application.ui.meals.function.repository.MealRepository

class FoodSearchViewModelFactory(
    private val foodRepository: FoodRepository,
    private val mealRepository: MealRepository,
    private val detectionRepository: DetectionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodSearchViewModel(foodRepository, mealRepository, detectionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}