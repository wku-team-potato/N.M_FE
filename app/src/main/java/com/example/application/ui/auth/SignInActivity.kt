package com.example.application.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.SessionManger
import com.example.application.databinding.ActivitySignInBinding
import com.example.application.ui.auth.functions.data.SignInResponse
import com.example.application.ui.auth.functions.repository.SignInRepository
import com.example.application.ui.auth.functions.viewmodel.SignInViewModel
import com.example.application.ui.auth.functions.viewmodel.SignInViewModelFactory
import com.example.application.ui.main.MainActivity

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }

    private lateinit var signInViewModel: SignInViewModel

    private lateinit var sessionManger: SessionManger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManger = SessionManger(this)

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        }

        val signInRepository = SignInRepository(RetrofitInstance.signInService)
        val viewModelFactory = SignInViewModelFactory(signInRepository)

        signInViewModel = ViewModelProvider(this, viewModelFactory).get(SignInViewModel::class.java)

        binding.signInButton.setOnClickListener {
            val username = binding.idEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                signInViewModel.signInUser(username, password)
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        observeSignInResult()
    }

    private fun observeSignInResult() {
        signInViewModel.signInResult.observe(this, Observer { result ->
            result?.let {
                Log.d("SignInActivity", "SignIn result observed: $it")
                if (it.success == "로그인 성공") {
                    handleLoginSuccess(it)
                } else {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Log.d("SignInActivity", "SignIn failed or null result")
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleLoginSuccess(result: SignInResponse) {
        val csrfToken = result.csrfToken
        sessionManger.saveCsrfToken(csrfToken)

        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}