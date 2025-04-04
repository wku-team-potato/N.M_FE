package com.example.application.functions.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.SignUpResponse
import com.example.application.data.repository.SignUpRepository
import kotlinx.coroutines.launch

class SignUpViewModel (private val signUpRepository: SignUpRepository) : ViewModel() {
    private val _signUpResult = MutableLiveData<SignUpResponse?>()
    val signUpResult: LiveData<SignUpResponse?> = _signUpResult

    fun signUpUser(username: String, password: String, nickname: String){
        viewModelScope.launch {
            val result = signUpRepository.signUpUser(username, password, nickname)
            _signUpResult.value = result
        }
    }
}