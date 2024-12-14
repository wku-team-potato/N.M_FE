package com.example.application.ui.view.reward

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.R
import com.example.application.databinding.ActivityPurchaseHistoryBinding
import com.example.application.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PurchaseHistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPurchaseHistoryBinding.inflate(layoutInflater) }
    private lateinit var adapter: PurchaseHistoryAdapter
    private val rewardService = RetrofitInstance.rewardService
    private val storeService = RetrofitInstance.storeService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        fetchStoreItems()
    }

    private fun fetchStoreItems() {
        lifecycleScope.launch {
            val storeResponse = storeService.getStoreItems()
            if (storeResponse.isSuccessful) {
                storeResponse.body()?.let { storeItems ->
                    adapter = PurchaseHistoryAdapter(storeItems)
                    binding.itemRecyclerView.layoutManager = LinearLayoutManager(this@PurchaseHistoryActivity)
                    binding.itemRecyclerView.adapter = adapter
                    fetchPurchaseHistory()
                }
            }
        }
    }

    private fun fetchPurchaseHistory() {
        lifecycleScope.launch {
            val response = rewardService.getPurchaseHistory()
            if (response.isSuccessful) {
                response.body()?.let { purchaseHistory ->
                    adapter.updateItems(purchaseHistory)
                }
            }
        }
    }
}