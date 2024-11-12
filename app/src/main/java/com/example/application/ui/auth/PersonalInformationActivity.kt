package com.example.application.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.databinding.ActivityPersonalInformationBinding
import com.example.application.ui.auth.functions.repository.PersonalInfoRepository
import com.example.application.ui.auth.functions.viewmodel.PersonalInfoViewModel
import com.example.application.ui.auth.functions.viewmodel.PersonalInfoViewModelFactory
import com.example.application.ui.main.MainActivity
import kotlinx.coroutines.launch

class PersonalInformationActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPersonalInformationBinding.inflate(layoutInflater) }

    private lateinit var personalInfoViewModel: PersonalInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val personalInfoRepository = PersonalInfoRepository(RetrofitInstance.personalInfoService)
        val viewModelFactory = PersonalInfoViewModelFactory(personalInfoRepository)
        personalInfoViewModel = ViewModelProvider(this, viewModelFactory).get(PersonalInfoViewModel::class.java)

        initUi()
    }

    private fun initUi() = with(binding) {
        doneButton.setOnClickListener {
            val height = heightEditText.text.toString()
            val weight = weightEditText.text.toString()

            if (height.isNotEmpty() && weight.isNotEmpty()) {
                submitPersonalInfo(height.toInt(), weight.toInt())
            } else {
                Toast.makeText(this@PersonalInformationActivity, "모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitPersonalInfo(height: Int, weight: Int) {
        lifecycleScope.launch {
            val result = personalInfoViewModel.submitPersonalInfo(height, weight)
            if (result.isSuccessful) {
                Toast.makeText(this@PersonalInformationActivity, "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            } else {
                Toast.makeText(this@PersonalInformationActivity, "정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("PersonalInformationActivity", "Failed to save personal info. Code: ${result.code()}, Error: ${result.errorBody()?.string()}")
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finishAffinity()
    }
}