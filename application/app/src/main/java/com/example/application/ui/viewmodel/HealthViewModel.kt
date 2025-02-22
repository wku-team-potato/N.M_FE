package com.example.application.ui.meals.function.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.request.MealUpdateRequest
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.repository.ProfileRepository
import com.example.application.ui.meals.function.data.HealthResponse
import com.example.application.ui.meals.function.data.MealDetailResponse
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.data.MealSummaryResponse
import com.example.application.ui.meals.function.data.MealTotalResponse
import com.example.application.ui.meals.function.repository.HealthRepository
import com.example.application.ui.meals.function.repository.MealRepository
import kotlinx.coroutines.launch

class HealthViewModel(private val healthRepository: HealthRepository,
    private val mealRepository: MealRepository,
    private val profileRepository: ProfileRepository) : ViewModel() {
    private val _mealList = MutableLiveData<List<MealResponse>>()
    private val _weightList = MutableLiveData<List<HealthResponse>>()
    private val _userInfo = MutableLiveData<ProfileResponse?>()

    private val _mealSummary = MutableLiveData<MealSummaryResponse>()
    val mealSummary: LiveData<MealSummaryResponse> = _mealSummary
    val mealList: LiveData<List<MealResponse>> get() = _mealList
    val weightList: LiveData<List<HealthResponse>> get() = _weightList
    val userInfo: LiveData<ProfileResponse?> get() = _userInfo

//    suspend fun getWeightList() {
//        try {
//            val response = healthRepository.getWeightList()
//            _weightList.postValue(response)
//        } catch (e: Exception) {
//            _weightList.postValue(emptyList())
//        }
//    }

    fun getUserInfo() {
        viewModelScope.launch {
            try {
                val response = profileRepository.getProfileInfo()
                _userInfo.value = response
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Error updating weight: ${e.message}")
                _userInfo.value = null
            }
        }
    }

    fun updateUserInfo(weight: Float) {
        val currentUserInfo = userInfo.value ?: return
        viewModelScope.launch {
            try {
                val updatedInfo = currentUserInfo.copy(weight = weight)
                val response = profileRepository.updateProfileInfo(updatedInfo)
                _userInfo.value = response
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Error updating weight: ${e.message}")
            }
        }
    }

    fun getWeightList() {
        viewModelScope.launch {
            try {
                val response = healthRepository.getWeightList()
                _weightList.value = response
            } catch (e: Exception) {
                Log.e("HealthViewModel", "Error fetching weight list", e)
                _weightList.value = emptyList()
            }
        }
    }

    fun getMealSummary(date: String) {
        viewModelScope.launch {
            try {
                val response = mealRepository.getMealSummary(date)
                Log.d("MealViewModel", "API Response: $response")

                _mealSummary.value = MealSummaryResponse(
                    breakfast = response.breakfast.roundValues(),
                    lunch = response.lunch.roundValues(),
                    dinner = response.dinner.roundValues(),
                    summary = response.summary.roundValues()
                )
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error loading meal summary: ${e.message}")
                // _mealSummary.value = null
            }
        }
    }

    fun fetchMealList(date: String) {
        viewModelScope.launch {
            try {
                val response = mealRepository.getMealList(date)
                _mealList.value = response
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error fetching meal list", e)
                _mealList.value = emptyList()
            }
        }
    }

    fun updateMeal(id: Int, foodId: Int, mealType: String, servingSize: Int, date: String) {
        viewModelScope.launch {
            try {
                val body = MealUpdateRequest(foodId, mealType, servingSize, date)
                mealRepository.updateMeal(id, body)
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error updating meal: ${e.message}")
            }
        }
    }

    fun deleteMeal(id: Int) {
        viewModelScope.launch {
            try {
                mealRepository.deleteMeal(id)
            } catch (e: Exception) {
                Log.e("MealViewModel", "Error deleting meal: ${e.message}")
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