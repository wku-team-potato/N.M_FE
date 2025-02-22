package com.example.application.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.data.repository.LeaderBoardRepository

class LeaderBoardViewModelFactory (private val repository: LeaderBoardRepository) :
    ViewModelProvider.Factory{

        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(LeaderBoardViewModel::class.java)) {
                return LeaderBoardViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }