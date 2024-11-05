package com.example.application.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.application.R
import com.example.application.databinding.ActivityPersonalInformationBinding
import com.example.application.ui.main.MainActivity

class PersonalInformationActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPersonalInformationBinding.inflate(layoutInflater) }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        initUi()
    }

    private fun initUi() = with(binding) {
        doneButton.setOnClickListener {
            startActivity(Intent(this@PersonalInformationActivity, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })

            finishAffinity()
        }
    }
}