package com.example.application.ui.view.main.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.application.R
import com.example.application.utils.RetrofitInstance
import com.example.application.databinding.FragmentRewardBinding
import com.example.application.data.repository.RewardRepository
import com.example.application.data.repository.ProfilePointRepository
import com.example.application.ui.view.reward.RewardActivity
import com.example.application.ui.viewmodel.RewardViewModel
import com.example.application.ui.viewmodel.RewardViewModelFactory

class RewardFragment : BaseFragment() {
    private var _binding: FragmentRewardBinding? = null
    private val binding get() = _binding!!
    private lateinit var rewardViewModel: RewardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    // 프래그먼트 복귀 시 포인트 다시 조회
    override fun onResume() {
        super.onResume()
        rewardViewModel.loadTotalPoints()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initViewModel()
        observeViewModel()
        rewardViewModel.loadTotalPoints()
    }

    private fun initViewModel() {
        val profilePointRepository = ProfilePointRepository(RetrofitInstance.profilePointService)
        val rewardRepository = RewardRepository(RetrofitInstance.rewardService)
        val factory = RewardViewModelFactory(profilePointRepository, rewardRepository)
        rewardViewModel = ViewModelProvider(this, factory).get(RewardViewModel::class.java)
    }

    private fun observeViewModel() = with(binding) {
        rewardViewModel.totalPoints.observe(viewLifecycleOwner) { points ->
            fragmentPointTextView.text = "$points p"
        }
    }

    private fun initUi() = with(binding) {

        myPointContainer.setOnClickListener {
            startActivity(Intent(requireContext(), RewardActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }
}