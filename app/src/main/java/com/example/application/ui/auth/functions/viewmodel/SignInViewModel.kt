package com.example.application.ui.auth.functions.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.ui.auth.functions.data.SignInResponse
import com.example.application.ui.auth.functions.repository.SignInRepository
import kotlinx.coroutines.launch

class SignInViewModel (private val signInRepository: SignInRepository) : ViewModel() {
    private val _signInResult = MutableLiveData<SignInResponse?>()
    val signInResult: LiveData<SignInResponse?> = _signInResult

    fun signInUser(username: String, password: String) {
        viewModelScope.launch {
            try {
                val result = signInRepository.signInUser(username, password)
                _signInResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _signInResult.value = null
            }
        }
    }

}