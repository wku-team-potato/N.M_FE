package com.example.application.ui.main.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.application.R
import com.example.application.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : BaseFragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
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
    }
}