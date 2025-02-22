package com.example.application.ui.view.reward

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.response.PurchaseHistoryResponse
import com.example.application.data.model.response.StoreResponse
import com.example.application.databinding.ItemPurchaseHistoryBinding
import com.example.application.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PurchaseHistoryAdapter(
    private val storeItems: List<StoreResponse>
) : RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseHistoryViewHolder>() {

    private val items = mutableListOf<PurchaseHistoryResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHistoryViewHolder {
        val binding = ItemPurchaseHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PurchaseHistoryViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PurchaseHistoryViewHolder, position: Int) {
        val purchase = items[position]
        val storeItem = storeItems.find { it.id == purchase.item_id.toInt() }

        with(holder.binding) {
            dateTextView.text = purchase.created_at
            itemTextView.text = storeItem?.name ?: "Unknown Item"
            pointTextView.text = "- ${storeItem?.price ?: 0}p"
        }
    }

    fun updateItems(newItems: List<PurchaseHistoryResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class PurchaseHistoryViewHolder(val binding: ItemPurchaseHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}