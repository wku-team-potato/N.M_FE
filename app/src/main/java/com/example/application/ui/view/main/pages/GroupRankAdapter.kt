package com.example.application.ui.view.main.pages

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.response.GroupRankingResponse
import com.example.application.databinding.ItemRankingBinding

class GroupRankAdapter:
    ListAdapter<GroupRankingResponse, GroupRankAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<GroupRankingResponse>() {
            override fun areItemsTheSame(
                oldItem: GroupRankingResponse,
                newItem: GroupRankingResponse
            ): Boolean {
                return oldItem.group_name == newItem.group_name
            }

            override fun areContentsTheSame(
                oldItem: GroupRankingResponse,
                newItem: GroupRankingResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class ViewHolder(private val binding: ItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupRankingResponse) {
            with(binding) {
                rankTextView.text = item.rank.toString()
                usernameTextView.text = item.group_name
                valueTextView.text = item.total_points.toString() + "점"
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