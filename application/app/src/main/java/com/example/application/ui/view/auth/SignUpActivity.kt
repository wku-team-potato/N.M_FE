package com.example.application.ui.view.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.application.R
import com.example.application.utils.RetrofitInstance
import com.example.application.databinding.ActivitySignUpBinding
import com.example.application.functions.viewmodel.SignUpViewModel
import com.example.application.ui.viewmodel.SignUpViewModelFactory
import com.example.application.data.repository.SignUpRepository

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signUpRepository = SignUpRepository(RetrofitInstance.signUpService)
        val viewModelFactory = SignUpViewModelFactory(signUpRepository)
        signUpViewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)

        binding.signUpButton.setOnClickListener{
            val username = binding.idEditText.text.toString()
            val password1 = binding.password1EditText.text.toString()
            val password2 = binding.password2EditText.text.toString()
            val nickname = binding.nameEditText.text.toString()

            if (password1 != password2){
                Toast.makeText(this, "비밀번호가 일치하지 않습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username.isEmpty() || password1.isEmpty() || password2.isEmpty() || nickname.isEmpty()){
                Toast.makeText(this, "모두 작성해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            signUpViewModel.signUpUser(username, password1, nickname)
        }

        signUpViewModel.signUpResult.observe(this, Observer { result ->
            if(result != null){
                startActivity(
                    Intent(
                        this@SignUpActivity,
                        SignInActivity::class.java
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    })
                finish()
            } else {
                Toast.makeText(this, "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        })


    }
}