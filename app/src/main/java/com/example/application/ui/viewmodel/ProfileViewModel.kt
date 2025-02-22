package com.example.application.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _latestRecord = MutableLiveData<ProfileResponse?>()
    val latestRecord: LiveData<ProfileResponse?> = _latestRecord

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> = _updateResult

    fun loadLatestRecord() {
        Log.d("ProfileViewModel", "loadLatestRecord called")
        viewModelScope.launch {
            try {
                val record = repository.getProfileInfo()
                if(record != null) {
                    Log.d("ProfileViewModel", "Record loaded: $record")
                    _latestRecord.value = record
                } else {
                    Log.e("ProfileViewModel", "No record found")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading record", e)
            }
        }
    }

    fun updateProfileInfo(updatedData: ProfileResponse) {
        viewModelScope.launch {
            try {
                val result = repository.updateProfileInfo(updatedData)
                _updateResult.value = result != null
            } catch (e: Exception) {
                _updateResult.value = false
            }
        }
    }
}