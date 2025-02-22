package com.example.application.ui.view.reward

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.application.R
import com.example.application.utils.RetrofitInstance
import com.example.application.databinding.ActivityRewardBinding
import com.example.application.data.repository.RewardRepository
import com.example.application.data.repository.ProfilePointRepository
import com.example.application.ui.viewmodel.RewardViewModel
import com.example.application.ui.viewmodel.RewardViewModelFactory

class RewardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRewardBinding.inflate(layoutInflater) }
    private lateinit var rewardViewModel: RewardViewModel
    private val rewardAdapter = RewardAdapter()

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
        initViewModel()
        initUi()
        observeViewModel()
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView.adapter = rewardAdapter
    }

    private fun initViewModel() {
        val profilePointRepository = ProfilePointRepository(RetrofitInstance.profilePointService)
        val rewardRepository = RewardRepository(RetrofitInstance.rewardService)
        val factory = RewardViewModelFactory(profilePointRepository, rewardRepository)
        rewardViewModel = ViewModelProvider(this, factory).get(RewardViewModel::class.java)

        rewardViewModel.loadTotalPoints() // 포인트 조회
        rewardViewModel.loadRewardRecords() // 리워드 기록 조회
    }

    private fun observeViewModel() = with(binding) {
        // 포인트 반영
        rewardViewModel.totalPoints.observe(this@RewardActivity) { points ->
            activityPointTextView.text = "$points p"
        }

        // 리워드 기록 반영
        rewardViewModel.rewardRecords.observe(this@RewardActivity) { records ->
            rewardAdapter.updateItems(records)
        }
    }
}