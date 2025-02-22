package com.example.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.RewardRepository
import com.example.application.data.repository.ProfilePointRepository


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