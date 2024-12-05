package com.example.application.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.ui.meals.function.data.MealDetailResponse
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.data.MealSummaryResponse
import com.example.application.ui.meals.function.data.MealTotalResponse
import com.example.application.ui.meals.function.repository.MealRepository
import kotlinx.coroutines.launch

class CalorieViewModel(private val mealRepository: MealRepository) : ViewModel() {

    private val _mealList = MutableLiveData<List<MealResponse>>()
    val mealList: LiveData<List<MealResponse>> get() = _mealList

    private val _mealSummary = MutableLiveData<MealSummaryResponse>()
    val mealSummary: LiveData<MealSummaryResponse> get() = _mealSummary

    fun getMealSummary(date: String) {
        viewModelScope.launch {
            try {
                val response = mealRepository.getMealSummary(date)
                _mealSummary.value = MealSummaryResponse(
                    breakfast = response.breakfast.roundValues(),
                    lunch = response.lunch.roundValues(),
                    dinner = response.dinner.roundValues(),
                    summary = response.summary.roundValues()
                )
            } catch (e: Exception) {
//                _mealSummary.value = null
            }
        }
    }

    fun getMealList(date: String) {
        viewModelScope.launch {
            try {
                val response = mealRepository.getMealList(date)
                _mealList.value = response
            } catch (e: Exception) {
                _mealList.value = emptyList()
            }
        }
    }

    private fun MealDetailResponse.roundValues() = MealDetailResponse(
        calorie = Math.round(calorie).toDouble(),
        carbohydrate = Math.round(carbohydrate).toDouble(),
        protein = Math.round(protein).toDouble(),
        fat = Math.round(fat).toDouble()
    )

    private fun MealTotalResponse.roundValues() = MealTotalResponse(
        calorie = Math.round(calorie).toDouble(),
        carbohydrate = Math.round(carbohydrate).toDouble(),
        protein = Math.round(protein).toDouble(),
        fat = Math.round(fat).toDouble()
    )

}