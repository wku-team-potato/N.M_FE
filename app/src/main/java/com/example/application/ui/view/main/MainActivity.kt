package com.example.application.ui.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.application.R
import com.example.application.databinding.ActivityMainBinding
import com.example.application.ui.view.main.pages.HealthFragment
import com.example.application.ui.view.main.pages.LeaderboardFragment
import com.example.application.ui.view.main.pages.RewardFragment
import com.example.application.ui.view.main.pages.StoreFragment
import com.example.application.ui.view.meals.FoodSearchActivity
import com.example.application.utils.HealthPermissions

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

//    private lateinit var sessionManager: SessionManager

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

//        sessionManager = SessionManager(this)
//        Log.d("csrfToken", sessionManager.getCsrfToken().toString())

        val toolbar = binding.toolbar

        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_my) {
//                showMyPage()
            }

            return@setOnMenuItemClickListener true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.viewPager) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = systemBars.bottom
            }
            insets
        }

        initUi()
    }

    private fun initUi() = with(binding) {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

//        viewPager.setPageTransformer { page, position ->
//            // 투명도 변화 속도 조정 (빠르게 전환)
//            page.alpha = 1 - Math.min(1f, Math.abs(position) * 1.5f)
//
//            // 슬라이드 효과 제거
//            page.translationX = -position * page.width
//        }

        bottomNavigationView.menu.getItem(2).isEnabled = false
        bottomNavigationView.setOnApplyWindowInsetsListener { view, insets ->
            val navHeight = view.height
            viewPager.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = navHeight
            }
//            view.updatePadding(bottom = 0)
            insets
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> {
                        bottomNavigationView.selectedItemId = R.id.action_health
                        toolbarTitle.text = "건강"
                    }
                    1 -> {
                        bottomNavigationView.selectedItemId = R.id.action_leaderboard
                        toolbarTitle.text = "리더보드"
                    }
                    2 -> {
                        bottomNavigationView.selectedItemId = R.id.action_reward
                        toolbarTitle.text = "리워드"
                    }
                    else -> {
                        bottomNavigationView.selectedItemId = R.id.action_store
                        toolbarTitle.text = "스토어"
                    }
                }
            }
        })
        viewPager.isUserInputEnabled = false

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_health -> {
                    viewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }

                R.id.action_leaderboard -> {
                    viewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }

                R.id.action_reward -> {
                    viewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }

                R.id.action_store -> {
                    viewPager.currentItem = 3
                    return@setOnItemSelectedListener true
                }
            }

            return@setOnItemSelectedListener false
        }

        addButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, FoodSearchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }

    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HealthFragment()
                1 -> LeaderboardFragment()
                2 -> RewardFragment()
                else -> StoreFragment()
            }
        }
    }
}