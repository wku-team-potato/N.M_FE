package com.example.application.ui.view.main.pages.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.application.R
import com.example.application.databinding.ActivityGroupDetailBinding

class GroupDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityGroupDetailBinding.inflate(layoutInflater) }

    companion object {
        private const val EXTRA_GROUP_ID = "extra_group_id"

        // Intent 생성 함수
        fun createIntent(context: Context, groupId: Int): Intent {
            return Intent(context, GroupDetailActivity::class.java).apply {
                putExtra(EXTRA_GROUP_ID, groupId)
            }
        }
    }

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

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        Log.d("GroupDetailActivity", intent.getIntExtra(EXTRA_GROUP_ID, -1).toString())
        Toast.makeText(this, intent.getIntExtra(EXTRA_GROUP_ID, -1).toString(), Toast.LENGTH_SHORT).show()
    }
}