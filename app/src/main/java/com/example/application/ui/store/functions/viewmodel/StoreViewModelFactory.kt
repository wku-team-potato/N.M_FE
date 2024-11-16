package com.example.application.ui.store.functions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.ui.store.functions.repository.ProfilePointRepository
import com.example.application.ui.store.functions.repository.StoreRepository

class StoreViewModelFactory(
    private val storeRepository: StoreRepository,
    private val profilePointRepository: ProfilePointRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            return StoreViewModel(storeRepository, profilePointRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}