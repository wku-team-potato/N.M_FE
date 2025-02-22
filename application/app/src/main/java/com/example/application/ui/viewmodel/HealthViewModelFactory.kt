package com.example.application.ui.meals.function.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.ProfileRepository
import com.example.application.ui.meals.function.repository.HealthRepository
import com.example.application.ui.meals.function.repository.MealRepository

class HealthViewModelFactory(private val healthRepository: HealthRepository,
    private val mealRepository: MealRepository,
    private val profileRepository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthViewModel(healthRepository, mealRepository, profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}