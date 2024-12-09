package com.example.application.ui.viewmodel.main.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.GroupMyRepository
import com.example.application.data.repository.ProfileRepository

class GroupViewModelFactory(private val profileRepository: ProfileRepository, private val groupRepository: GroupMyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(profileRepository, groupRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}