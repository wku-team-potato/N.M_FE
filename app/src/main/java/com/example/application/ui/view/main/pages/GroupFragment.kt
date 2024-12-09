package com.example.application.ui.view.main.pages

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.application.R
import com.example.application.data.repository.GroupMyRepository
import com.example.application.data.repository.ProfileRepository
import com.example.application.data.service.GroupMyService
import com.example.application.databinding.FragmentGroupBinding
import com.example.application.ui.view.main.GroupPagerAdapter
import com.example.application.ui.viewmodel.main.group.GroupViewModel
import com.example.application.ui.viewmodel.main.group.GroupViewModelFactory
import com.example.application.utils.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroupPagerAdapter

    private val groupRepository: GroupMyRepository by lazy {
        GroupMyRepository(RetrofitInstance.groupMyService)
    }

    private val profileRepository: ProfileRepository by lazy {
        ProfileRepository(RetrofitInstance.profileService)
    }

    private val viewModel: GroupViewModel by lazy {
        val factory = GroupViewModelFactory(profileRepository, groupRepository)
        ViewModelProvider(this, factory).get(GroupViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadMyInfo()
        viewModel.loadMyGroups()
        viewModel.loadAllGroups()
    }

    override fun onResume() {
        super.onResume()

        binding.viewPager.visibility = View.VISIBLE
        viewModel.loadMyGroups()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.viewPager) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val navBarHeight = systemBars.bottom
            v.setPadding(0, 0, 0, navBarHeight) // 하단 패딩 설정
            insets
        }

        adapter = GroupPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "내 그룹"
                1 -> "그룹 찾기"
                else -> null
            }
        }.attach()

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        binding.viewPager.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getSharedViewModel(): GroupViewModel {
        return viewModel
    }
}