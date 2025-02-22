package com.example.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.StoreResponse
import com.example.application.data.repository.ProfilePointRepository
import com.example.application.data.repository.StoreRepository
import kotlinx.coroutines.launch

class StoreViewModel(
    private val repository: StoreRepository,
    private val profilePointRepository: ProfilePointRepository
) : ViewModel() {

    // 상품 리스트 LiveData
    private val _items = MutableLiveData<List<StoreResponse>>()
    val items: LiveData<List<StoreResponse>> = _items

    // 포인트 LiveData
    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> = _points

    // 구매 결과 메시지 LiveData
    private val _buyResult = MutableLiveData<String>()
    val buyResult: LiveData<String> = _buyResult

    fun loadItems() {
        Log.d("StoreViewModel", "Attempting to load items")
        viewModelScope.launch {
            try {
                val result = repository.getItems() ?: emptyList()
                Log.d("StoreViewModel", "Items loaded successfully: ${result.size} items")
                _items.value = result // LiveData로 업데이트
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error loading items", e)
            }
        }
    }

    fun loadPoints(){
        viewModelScope.launch {
            try{
                val totalPoints = profilePointRepository.getTotalPoints()
                _points.value = totalPoints // LiveData 업데이트
            } catch (e: Exception) {
                _points.value = 0
            }
        }
    }

    fun buyItem(itemId: Int) {
        viewModelScope.launch {
            try {
                val response = profilePointRepository.buyItem(itemId)
                if (response != null) {
                    if (response.isSuccessful) {
                        Log.d("StoreViewModel", "Purchase successful: ${response.body()?.message}")
                        _buyResult.value = "구매 성공!" // LiveData로 구매 결과 메시지 업데이트
                        loadPoints() // 포인트 업데이트
                    } else {
                        when (response.code()) {
                            400 -> {
                                Log.e("StoreViewModel", "Purchase failed: Insufficient points")
                                _buyResult.value = "포인트가 부족합니다!"
                            }
                            else -> {
                                val errorMessage = response.errorBody()?.string()
                                Log.e("StoreViewModel", "Purchase failed: $errorMessage")
                                _buyResult.value = "구매 실패: 서버 오류가 발생했습니다."
                            }
                        }
                    }
                } else {
                    Log.e("StoreViewModel", "Purchase failed: Response is null")
                    _buyResult.value = "구매 실패: 서버로부터 응답이 없습니다."
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error during purchase", e)
                _buyResult.value = "구매 실패: 네트워크 오류가 발생했습니다."
            }
        }
    }

    // 구매 결과 메시지를 초기화하여 UI가 불필요하게 업데이트되지 않도록 함
    fun resetBuyResult() {
        _buyResult.value = null
    }
}