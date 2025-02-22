package com.example.application.ui.view.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.R
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.repository.FirstPersonalInfoRepository
import com.example.application.data.repository.LogoutRepository
import com.example.application.data.repository.ProfileRepository
import com.example.application.databinding.ActivitySettingsBinding
import com.example.application.databinding.DialogMyPageBinding
import com.example.application.ui.view.auth.SignInActivity
import com.example.application.ui.viewmodel.SettingsViewModel
import com.example.application.ui.viewmodel.SettingsViewModelFactory
import com.example.application.utils.RetrofitInstance
import com.example.application.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        val sessionManager = SessionManager(this)

        val repository = LogoutRepository(RetrofitInstance.logoutService)
        val profileRepository = ProfileRepository(RetrofitInstance.profileService)
        val personalInfoRepository = FirstPersonalInfoRepository(RetrofitInstance.firstPersonalInfoService)
        val factory = SettingsViewModelFactory(sessionManager, repository, profileRepository, personalInfoRepository)
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

        binding.editUserBtn.setOnClickListener {
            editDialog()
        }

        binding.btnLogout.setOnClickListener {
            settingsViewModel.logoutUser()
        }


        initProfileInfo()
        initUi()
    }

    private fun initProfileInfo() {
        settingsViewModel.getUserInfo()

        settingsViewModel.userName.observe(this) {
            binding.tvUserName.text = it + " 님"
        }

        settingsViewModel.userHeight.observe(this) {
            binding.tvUserHeight.text = String.format("%.1f", it) + "cm"
        }

        settingsViewModel.userWeight.observe(this) {
            binding.tvUserWeight.text = String.format("%.1f", it) + "kg"
        }

        settingsViewModel.userPoint.observe(this) {
            binding.tvUserPoint.text = it.toString() + "P"
        }
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun editDialog() {
        val binding_dialog = DialogMyPageBinding.inflate(LayoutInflater.from(this))

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(binding_dialog.root)
            .show()

        with(binding_dialog) {
            nicknameEditText.hint = settingsViewModel.userName.value
            heightEditText.hint = settingsViewModel.userHeight.value.toString()
            weightEditText.hint = settingsViewModel.userWeight.value.toString()

            editButton.setOnClickListener {
                editButton.isVisible = false
                doneButton.isVisible = true
                nicknameEditText.isEnabled = true
                heightEditText.isEnabled = true
                weightEditText.isEnabled = true
            }

            doneButton.setOnClickListener {
                nicknameEditText.isEnabled = false
                heightEditText.isEnabled = false
                weightEditText.isEnabled = false

                val updatedProfile = ProfileResponse(
                    username = nicknameEditText.text.toString()
                        .takeIf { it.isNotEmpty() } ?: settingsViewModel.userName.value ?: "",
                    height = heightEditText.text.toString()
                        .toFloatOrNull() ?: settingsViewModel.userHeight.value ?: 0f,
                    weight = weightEditText.text.toString()
                        .toFloatOrNull() ?: settingsViewModel.userWeight.value ?: 0f
                )

                settingsViewModel.updateProfileInfo(updatedProfile)

                settingsViewModel.updateResult.observe(this@SettingsActivity) { success ->
                    when (success) {
                        true -> {
                            Toast.makeText(this@SettingsActivity, "프로필 수정 완료", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        false -> {
                            Toast.makeText(this@SettingsActivity, "프로필 수정 실패", Toast.LENGTH_SHORT).show()
                        }
                        null -> {
                            // 아무 작업도 안 함 (상태 초기화 시)
                        }
                    }
                }

                dialog.dismiss()
            }
        }
    }
}