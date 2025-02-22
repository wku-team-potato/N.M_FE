package com.example.application.functions.viewmodel

import android.se.omapi.Session
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application.data.model.response.SignInResponse
import com.example.application.data.repository.SignInRepository
import com.example.application.utils.SessionManager
import kotlinx.coroutines.launch

class SignInViewModel (
    private val sessionManager: SessionManager,
    private val signInRepository: SignInRepository) : ViewModel() {
    private val _signInResult = MutableLiveData<SignInResponse?>()
    val signInResult: LiveData<SignInResponse?> = _signInResult

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    // 세션 확인
    fun checkSession() {
        val sessionId = sessionManager.getSessionId()
        if (sessionId != null) {
            _isLoggedIn.postValue(true)
        } else {
            _isLoggedIn.postValue(false)
        }
    }

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