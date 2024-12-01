package com.example.application.ui.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.response.ConsecutiveGoalsRank
import com.example.application.databinding.ItemRankingBinding

class ConsecutiveGoalAdapter :
    ListAdapter<ConsecutiveGoalsRank, ConsecutiveGoalAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<ConsecutiveGoalsRank>() {
            override fun areItemsTheSame(
                oldItem: ConsecutiveGoalsRank,
                newItem: ConsecutiveGoalsRank
            ): Boolean {
                return oldItem.username == newItem.username
            }

            override fun areContentsTheSame(
                oldItem: ConsecutiveGoalsRank,
                newItem: ConsecutiveGoalsRank
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class ViewHolder(private val binding: ItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ConsecutiveGoalsRank) {
            with(binding) {
                rankTextView.text = item.rank.toString()
                usernameTextView.text = item.username
                valueTextView.text = "${item.days} 연속 목표 달성"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}