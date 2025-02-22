package com.example.application.ui.view.reward

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.ItemRewardBinding
import com.example.application.data.model.response.RewardResponse

class RewardAdapter : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    private val items = mutableListOf<RewardResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val binding = ItemRewardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RewardViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            dateTextView.text = item.created_at
            pointTextView.text = "${if (item.points_changed > 0) "+" else ""}${item.points_changed}p"
            descriptionTextView.text = item.description
        }
    }

    fun updateItems(newItems: List<RewardResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class RewardViewHolder(val binding: ItemRewardBinding) : RecyclerView.ViewHolder(binding.root)
}