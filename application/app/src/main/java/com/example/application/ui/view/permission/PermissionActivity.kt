package com.example.application.ui.view.permission

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.health.connect.client.HealthConnectClient
import com.example.application.R
import com.example.application.databinding.ActivityPermissionBinding
import com.example.application.ui.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionController = HealthConnectClient.getOrCreate(this).permissionController
    }

    private  val permissionRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}