package com.example.application.ui.reward.function.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.ui.reward.function.data.RewardResponse
import com.example.application.ui.reward.function.repository.RewardRepository
import com.example.application.ui.store.functions.repository.ProfilePointRepository
import kotlinx.coroutines.launch

class RewardViewModel(
    private val profilePointRepository: ProfilePointRepository,
    private val rewardRepository: RewardRepository
) : ViewModel() {

    private val _totalPoints = MutableLiveData<Int>()
    val totalPoints: LiveData<Int> = _totalPoints

    private val _rewardRecords = MutableLiveData<List<RewardResponse>>()
    val rewardRecords: LiveData<List<RewardResponse>> = _rewardRecords

    // 포인트 조회
    fun loadTotalPoints() {
        viewModelScope.launch {
            try {
                val totalPoints = profilePointRepository.getTotalPoints()
                _totalPoints.value = totalPoints
            } catch (e: Exception) {
                _totalPoints.value = 0
            }
        }
    }

    // 리워드 기록 조회
    fun loadRewardRecords() {
        viewModelScope.launch {
            try {
                val rewards = rewardRepository.getRewards()
                _rewardRecords.value = rewards
            } catch (e: Exception) {
                _rewardRecords.value = emptyList()
            }
        }
    }
}