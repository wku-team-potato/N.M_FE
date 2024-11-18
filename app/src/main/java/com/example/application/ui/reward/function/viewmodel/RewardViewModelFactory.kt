package com.example.application.ui.reward.function.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.ui.reward.function.repository.RewardRepository
import com.example.application.ui.store.functions.repository.ProfilePointRepository


class RewardViewModelFactory(
    private val profilePointRepository: ProfilePointRepository,
    private val rewardRepository: RewardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RewardViewModel::class.java)) {
            return RewardViewModel(profilePointRepository, rewardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}