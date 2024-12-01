package com.example.application.ui.view.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.application.R
import com.example.application.utils.RetrofitInstance
import com.example.application.utils.RetrofitInstance.firstPersonalInfoService
import com.example.application.utils.SessionManager
import com.example.application.databinding.ActivitySignInBinding
import com.example.application.data.model.response.SignInResponse
import com.example.application.functions.viewmodel.SignInViewModel
import com.example.application.functions.viewmodel.SignInViewModelFactory
import com.example.application.data.repository.FirstPersonalInfoRepository
import com.example.application.data.repository.SignInRepository
import com.example.application.ui.view.main.MainActivity
import com.example.application.utils.HealthPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }

    private lateinit var signInViewModel: SignInViewModel

    private lateinit var sessionManager: SessionManager

    private lateinit var healthConnectClient: HealthConnectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

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

        CoroutineScope(Dispatchers.Main).launch {
            connectHealthData()
        }
        observeSignInResult()
    }

    private fun observeSignInResult() {
        signInViewModel.signInResult.observe(this, Observer { result ->
            result?.let {
                Log.d("SignInActivity", "SignIn result observed: $it")
                if (it.success == "로그인 성공") {
                    handleLoginSuccess(it)
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
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
        val sessionId = result.sessionId
        sessionManager.saveCsrfToken(csrfToken)
        sessionManager.saveSessionId(sessionId)
        Log.d("SessionManager", "Stored CSRF Token: ${sessionManager.getCsrfToken()}")
        Log.d("SessionManager", "Stored Session ID: ${sessionManager.getSessionId()}")
        checkRecord()
    }

    private fun checkRecord() {
        val firstPersonalInfoRepository =
            FirstPersonalInfoRepository(
                firstPersonalInfoService
            )

        lifecycleScope.launch {
            try {
                val personalInfo = firstPersonalInfoRepository.getFirstInfo()
                Log.d("SignInActivity", "Received personalInfo: $personalInfo")

                if (personalInfo?.created_at != null) {
                    // 개인정보 데이터가 존재하는 경우 홈 화면으로 이동
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    // 개인정보 데이터가 존재하지 않는 경우 개인정보 입력창으로 이동
                    val intent = Intent(this@SignInActivity, com.example.application.ui.view.auth.PersonalInformationActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SignInActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun connectHealthData() {

        // HealthConnect 앱 설치 여부 확인
        val availabilityStatus = HealthConnectClient.getSdkStatus(this, "com.google.android.apps.healthdata")
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            this.finish()
            openPlayStoreForHealthConnect()
        }

        // HealthConnect 앱 업데이트 필요 여부 확인
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            this.finish()
            openPlayStoreForHealthConnect()
        }

        healthConnectClient = HealthConnectClient.getOrCreate(this)
        requestPermission.launch(HealthPermissions.PERMISSIONS)
    }

    private fun openPlayStoreForHealthConnect() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private val requestPermission = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(HealthPermissions.PERMISSIONS)) {
            Log.d("HealthFragment", "Permission granted")
        } else {
            Log.d("HealthFragment", "Permission denied")
        }
    }
}