package com.example.application.ui.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.response.ConsecutiveAttendanceRank
import com.example.application.data.model.response.CumulativeAttendanceRank
import com.example.application.databinding.ItemRankingBinding

class ConsecutiveAttendAdapter :
    ListAdapter<ConsecutiveAttendanceRank, ConsecutiveAttendAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<ConsecutiveAttendanceRank>() {
            override fun areItemsTheSame(
                oldItem: ConsecutiveAttendanceRank,
                newItem: ConsecutiveAttendanceRank
            ): Boolean {
                // 각 항목의 고유 ID나 키를 비교
                return oldItem.username == newItem.username
            }

            override fun areContentsTheSame(
                oldItem: ConsecutiveAttendanceRank,
                newItem: ConsecutiveAttendanceRank
            ): Boolean {
                // 항목의 전체 내용을 비교
                return oldItem == newItem
            }
        }
    ) {

    // ViewHolder 정의
    inner class ViewHolder(private val binding: ItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ConsecutiveAttendanceRank) {
            with(binding) {
                rankTextView.text = item.rank.toString()
                usernameTextView.text = item.username
                valueTextView.text = "${item.days}일 연속"
            }
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}
