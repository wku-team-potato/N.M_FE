package com.example.application.ui.view.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.application.R
import com.example.application.data.repository.LogoutRepository
import com.example.application.databinding.ActivitySettingsBinding
import com.example.application.ui.view.auth.SignInActivity
import com.example.application.ui.viewmodel.SettingsViewModel
import com.example.application.ui.viewmodel.SettingsViewModelFactory
import com.example.application.utils.RetrofitInstance
import com.example.application.utils.SessionManager

class SettingsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_primary)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        Log.d("Logout", "Settings activity started")

        val sessionManager = SessionManager(this)

        val repository = LogoutRepository(RetrofitInstance.logoutService)
        val factory = SettingsViewModelFactory(sessionManager, repository)
        settingsViewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        settingsViewModel.logoutResult.observe(this) {
            Log.d("Logout", it.success)
            if (it.success == "로그아웃") {
                Log.d("Logout", "Logout button clicked")
                SessionManager(this).clearSession()
                Log.d("Logout", "Session cleared")
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                Log.d("Logout", "Activity should be changing")
            }
        }

        binding.btnLogout.setOnClickListener {
            settingsViewModel.logoutUser()
        }

    }
}