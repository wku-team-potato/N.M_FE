package com.example.application.ui.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.response.CumulativeGoalsRank
import com.example.application.databinding.ItemRankingBinding

class CumulativeGoalAdapter :
    ListAdapter<CumulativeGoalsRank, CumulativeGoalAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<CumulativeGoalsRank>() {
            override fun areItemsTheSame(
                oldItem: CumulativeGoalsRank,
                newItem: CumulativeGoalsRank
            ): Boolean {
                return oldItem.username == newItem.username
            }

            override fun areContentsTheSame(
                oldItem: CumulativeGoalsRank,
                newItem: CumulativeGoalsRank
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class ViewHolder(private val binding: ItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CumulativeGoalsRank) {
            with(binding) {
                rankTextView.text = item.rank.toString()
                usernameTextView.text = item.username
                valueTextView.text = "${item.days} 목표 달성"
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