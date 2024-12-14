package com.example.application.ui.view.main.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.utils.RetrofitInstance
import com.example.application.databinding.FragmentStoreBinding
import com.example.application.data.repository.StoreRepository
import com.example.application.data.repository.ProfilePointRepository
import com.example.application.ui.view.reward.PurchaseHistoryActivity
import com.example.application.ui.view.reward.RewardActivity
import com.example.application.ui.view.store.StoreAdapter
import com.example.application.ui.view.store.StoreItemDetailsActivity
import com.example.application.ui.viewmodel.StoreViewModel
import com.example.application.ui.viewmodel.StoreViewModelFactory

class StoreFragment : BaseFragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var storeViewModel: StoreViewModel
    private lateinit var storeAdapter: StoreAdapter

    // 상세 페이지에서 결과를 반환
    private val detailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                storeViewModel.loadPoints() // 포인트 새로고침
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    // 프래그먼트 복귀 시 포인트 다시 조회
    override fun onResume() {
        super.onResume()
        storeViewModel.loadPoints()
    }

    private fun initUi() = with(binding) {
        myPoint.setOnClickListener {
            val intent = Intent(requireContext(), RewardActivity::class.java)
            startActivity(intent)
        }

        purchaseHistory.setOnClickListener {
            val intent = Intent(requireContext(), PurchaseHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initViewModel()
        setupRecyclerView()
        observeViewModel()
        storeViewModel.loadItems()
        storeViewModel.loadPoints()
    }

    private fun initViewModel() {
        val storeRepository = StoreRepository(RetrofitInstance.storeService)
        val profilePointRepository = ProfilePointRepository(RetrofitInstance.profilePointService)
        val factory = StoreViewModelFactory(storeRepository, profilePointRepository)
        storeViewModel = ViewModelProvider(this, factory).get(StoreViewModel::class.java)
    }


    // UI 업데이트
    private fun observeViewModel() {
        // 아이템 목록
        storeViewModel.items.observe(viewLifecycleOwner) { items ->
            Log.d("StoreFragment", "Loaded items: ${items.size}")
            storeAdapter.updateItems(items)
        }

        // 포인트
        storeViewModel.points.observe(viewLifecycleOwner) { points ->
            Log.d("StoreFragment", "Loaded points: $points")
            binding.tvPoint.text = "${points} p"
        }
    }

    // RecyclerView 초기화
    private fun setupRecyclerView() {
        // Adapter 생성
        storeAdapter = StoreAdapter { item ->

            // 상세페이지 시작
            val intent = Intent(requireContext(), StoreItemDetailsActivity::class.java)
            intent.putExtra("data", item)
            detailsLauncher.launch(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = storeAdapter
    }


}