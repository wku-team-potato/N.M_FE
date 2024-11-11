package com.example.application.ui.auth

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
import com.example.application.RetrofitInstance
import com.example.application.databinding.ActivitySignUpBinding
import com.example.application.ui.auth.functions.repository.SignUpRepository
import com.example.application.ui.auth.functions.service.SignUpService
import com.example.application.ui.auth.functions.viewmodel.SignUpViewModel
import com.example.application.ui.auth.functions.viewmodel.SignUpViewModelFactory

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
            val password = binding.password1EditText.text.toString()
            val nickname = binding.nameEditText.text.toString()

            signUpViewModel.signUpUser(username, password, nickname)
        }

        signUpViewModel.signUpResult.observe(this, Observer { result ->
            if(result != null){
                startActivity(
                    Intent(
                        this@SignUpActivity,
                        PersonalInformationActivity::class.java
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    })

                finishAffinity()
            } else {
                /***
                 * 회원가입 실패 시 액션
                 */
                Toast.makeText(this, "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        })

//        initUi()
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        signUpButton.setOnClickListener {
            startActivity(
                Intent(
                    this@SignUpActivity,
                    PersonalInformationActivity::class.java
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })

            finishAffinity()
        }
    }
}