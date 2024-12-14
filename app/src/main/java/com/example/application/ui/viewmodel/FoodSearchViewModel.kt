package com.example.application.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.DetectionResponse
import com.example.application.data.repository.DetectionRepository
import com.example.application.data.repository.FoodRepository
import com.example.application.ui.meals.function.data.FoodResponse
import com.example.application.ui.meals.function.repository.MealRepository
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodSearchViewModel(
    private val foodRepository: FoodRepository,
    private val mealRepository: MealRepository,
    private val detectionRepository: DetectionRepository
) : ViewModel() {

    private val _foodList = MutableLiveData<List<FoodResponse>>()
    val foodList: LiveData<List<FoodResponse>> get() = _foodList

    private val _operationResult = MutableLiveData<String>()
    val operationResult: LiveData<String> get() = _operationResult

    private val _detectionResult = MutableLiveData<DetectionResponse>()
    val detectionResult: LiveData<DetectionResponse> get() = _detectionResult

    private val _selectedFoods = MutableLiveData<List<FoodResponse>>()
    val selectedFoods: LiveData<List<FoodResponse>> get() = _selectedFoods


    /**
     * 음식 선택 토글
     */
    fun toggleFood(food: FoodResponse) {
        val currentList = _selectedFoods.value ?: mutableListOf()
        if (currentList.contains(food)) {
            _selectedFoods.postValue(currentList - food)
        } else {
            _selectedFoods.postValue(currentList + food)
        }
    }

    /**
     * 이미지 업로드 및 결과 처리
     */
    fun uploadImage(filePath: String, mealType: String, date: String) {
        Log.d("FoodSearchViewModel", "Uploading image: $filePath")
        Log.d("FoodSearchViewModel", "Meal type: $mealType, Date: $date")

        detectionRepository.uploadImage(filePath) { response ->
            response?.let {
                val detectedLabels = it.result.mapNotNull { result -> result.label?.toInt() }
                    .filter { label -> label != 0 } // 0을 제외한 유효한 라벨만 추출

                if (detectedLabels.isNotEmpty()) {
                    viewModelScope.launch {
                        try {
                            // 각 라벨로 FoodResponse 검색
                            val foodResults = detectedLabels.mapNotNull { label ->
                                try {
                                    Log.d("FoodSearchViewModel", "Searching food for label: $label")
                                    foodRepository.searchFoodById(label)
                                } catch (e: Exception) {
                                    Log.e("FoodSearchViewModel", "Error fetching food for label $label: ${e.message}")
                                    null
                                }
                            }

                            // 중복 제거: 기존 리스트에 없는 항목만 추가
                            val updatedSelectedFoods = _selectedFoods.value?.toMutableList() ?: mutableListOf()
                            val newFoods = foodResults.filter { newFood ->
                                updatedSelectedFoods.none { existingFood -> existingFood.food_id == newFood.food_id }
                            }
                            updatedSelectedFoods.addAll(newFoods)

                            // 리스트 업데이트
                            _selectedFoods.value = updatedSelectedFoods

                            // 성공 로그 출력
                            Log.d("FoodSearchViewModel", "Updated selected foods: $updatedSelectedFoods")
                            Log.d("FoodSearchViewModel", "_selectedFoods: ${_selectedFoods.value}")

                            // 데이터 업데이트 후 addSelectedFoods 호출
                            addSelectedFoods(mealType, date)
                        } catch (e: Exception) {
                            Log.e("FoodSearchViewModel", "Failed to fetch food for labels: ${e.message}")
                            _selectedFoods.postValue(emptyList())
                        }
                    }
                } else {
                    Log.e("FoodSearchViewModel", "No valid labels detected in response")
                }
            } ?: Log.e("FoodSearchViewModel", "Image upload failed")
        }
    }



    /**
     * 음식 리스트 검색
     */
    fun fetchFoods(foodName: String) = viewModelScope.launch {
        executeWithErrorHandling(
            block = {
                val foodList = foodRepository.searchFoodList(foodName)
                Log.d("FoodSearchViewModel", "Fetched food list: $foodList")
                _foodList.postValue(foodList)
            },
            onError = {
                _foodList.postValue(emptyList())
            }
        )
    }

    /**
     * 선택된 음식 추가
     */
    fun addSelectedFoods(mealType: String, date: String) = viewModelScope.launch {
        val foods = _selectedFoods.value.orEmpty()

        Log.d("FoodSearchViewModel", "Adding foods: $foods to mealType: $mealType, date: $date")

        if (foods.isEmpty()) {
            _operationResult.postValue("선택된 음식이 없습니다.")
            return@launch
        }

        executeWithErrorHandling(
            block = {
                foods.forEach { food ->
                    mealRepository.addMeal(
                        food.food_id,
                        food.serving_size,
                        mealType,
                        date
                    )
                }
                _operationResult.postValue("음식 ${foods.size}개가 성공적으로 추가되었습니다!")
            },
            onError = {
                _operationResult.postValue("음식 추가 실패: ${it.message}")
            }
        )
    }

    /**
     * 에러 핸들링과 실행
     */
    private suspend fun executeWithErrorHandling(
        block: suspend () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            block()
        } catch (e: Exception) {
            onError(e)
        }
    }

    /**
     * 성공 메시지 생성
     */
    private fun generateSuccessMessage(selectedFood: FoodResponse, mealType: String, date: String): String {
        val formattedDate = LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MM월 dd일"))
        val mealTimeText = when (mealType) {
            "breakfast" -> "아침"
            "lunch" -> "점심"
            "dinner" -> "저녁"
            else -> ""
        }
        return "$formattedDate $mealTimeText\n${selectedFood.food_name} 추가!"
    }
}
