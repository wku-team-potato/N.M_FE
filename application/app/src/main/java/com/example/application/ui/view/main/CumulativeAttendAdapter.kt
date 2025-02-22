package com.example.application.ui.view.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.response.CumulativeAttendanceRank
import com.example.application.databinding.ItemRankingBinding

class CumulativeAttendAdapter :
    ListAdapter<CumulativeAttendanceRank, CumulativeAttendAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<CumulativeAttendanceRank>() {
            override fun areItemsTheSame(
                oldItem: CumulativeAttendanceRank,
                newItem: CumulativeAttendanceRank
            ): Boolean {
                return oldItem.username == newItem.username
            }

            override fun areContentsTheSame(
                oldItem: CumulativeAttendanceRank,
                newItem: CumulativeAttendanceRank
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class ViewHolder(private val binding: ItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CumulativeAttendanceRank) {
            with(binding) {
                rankTextView.text = item.rank.toString()
                usernameTextView.text = item.username
                valueTextView.text = "${item.days}일 누적"
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