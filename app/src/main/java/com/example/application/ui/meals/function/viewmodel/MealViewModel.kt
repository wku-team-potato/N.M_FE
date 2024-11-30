package com.example.application.ui.meals.function.viewmodel

import android.util.Log
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

class MealViewModel(private val repository: MealRepository) : ViewModel() {
    private val _mealSummary = MutableLiveData<MealSummaryResponse>()
    private val _mealList = MutableLiveData<List<MealResponse>>()

    val mealSummary: LiveData<MealSummaryResponse> = _mealSummary
    val mealList: LiveData<List<MealResponse>> get() = _mealList

    fun loadMealSummary(date: String) {
        viewModelScope.launch {
            try {
                Log.d("MealViewModel", "loadMealSummary called with date: $date") // 로그 추가
                val response = repository.getMealSummary(date)
                Log.d("MealViewModel", "API Response: $response")

                // 반올림 처리된 데이터
                _mealSummary.value = MealSummaryResponse(
                    breakfast = response.breakfast.roundValues(),
                    lunch = response.lunch.roundValues(),
                    dinner = response.dinner.roundValues(),
                    summary = response.summary.roundValues()
                )
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error loading meal summary: ${e.message}")
                _mealSummary.value = null
            }
        }
    }

    fun fetchMealList(date: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMealList(date)
                _mealList.value = response
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error fetching meal list", e)
                _mealList.value = emptyList()
            }
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