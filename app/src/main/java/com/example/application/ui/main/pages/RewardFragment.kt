package com.example.application.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.application.R
import com.example.application.databinding.FragmentRewardBinding
import com.example.application.ui.reward.RewardActivity

class RewardFragment : BaseFragment() {
    private var _binding: FragmentRewardBinding? = null
    private val binding
        get() = _binding!!

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() = with(binding) {
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_my) {
                showMyPage()
            }

            return@setOnMenuItemClickListener true
        }

        myPointContainer.setOnClickListener {
            startActivity(Intent(requireContext(), RewardActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }
}