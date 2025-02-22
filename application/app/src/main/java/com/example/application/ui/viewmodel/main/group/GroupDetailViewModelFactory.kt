package com.example.application.ui.viewmodel.main.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.GroupMyRepository
import com.example.application.data.repository.ProfileRepository

class GroupDetailViewModelFactory(private val groupMyRepository: GroupMyRepository, private val profileRepository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupDetailViewModel::class.java)) {
            return GroupDetailViewModel(groupMyRepository, profileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}