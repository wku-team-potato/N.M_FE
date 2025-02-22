package com.example.application.ui.view.main.pages.group

import android.graphics.Color
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.groups.response.GroupResponse
import com.example.application.data.model.response.ProfileResponse_2
import com.example.application.databinding.ItemMyGroupBinding
import com.example.application.ui.viewmodel.main.group.GroupViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyGroupAdapter(
    private var groupList: List<GroupViewModel.GroupWithProfile>,
    private var username: String,
    private val onDetailClick: (GroupResponse) -> Unit,
    private val onDeleteClick: (GroupResponse) -> Unit
) : RecyclerView.Adapter<MyGroupAdapter.MyGroupViewHolder>() {

    inner class MyGroupViewHolder(private val binding: ItemMyGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: GroupViewModel.GroupWithProfile) {
            // 바인딩을 통해 데이터 설정

            val creator_text = "생성자 : " + group.profile.username
            val date_text = "생성일 : " + group.group.created_at.split("T")[0]

            val creator_text_spannable = Spannable.Factory.getInstance().newSpannable(creator_text)
            val date_text_spannable = Spannable.Factory.getInstance().newSpannable(date_text)

            creator_text_spannable.setSpan(
                android.text.style.RelativeSizeSpan(0.75f), // 글씨 크기를 1.5배로
                0, // 시작 위치
                5, // 끝 위치 ("생성일 :"의 길이)
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            date_text_spannable.setSpan(
                android.text.style.RelativeSizeSpan(0.75f), // 글씨 크기를 1.5배로
                0, // 시작 위치
                5, // 끝 위치 ("생성일 :"의 길이)
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

//            if (group.profile.username == username) {
//                binding.deleteButton.visibility = ViewGroup.VISIBLE
//                binding.deleteButton.setOnClickListener {
//                    onDeleteClick(group.group)
//                }
//            } else {
//                binding.deleteButton.visibility = ViewGroup.GONE
//            }

//            binding.deleteButton.visibility = ViewGroup.VISIBLE
            binding.deleteButton.setOnClickListener {
                onDeleteClick(group.group)
            }

            binding.groupNameTextView.text = group.group.name // 그룹 이름 설정
            binding.creatorTextView.text = creator_text_spannable// 그룹 생성자 설정
            binding.pointTextView.text = date_text_spannable // 그룹 생성일 설정
            binding.cardViewMyGroup.setOnClickListener {
                onDetailClick(group.group) // 클릭 이벤트 처리
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGroupViewHolder {
        val binding = ItemMyGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyGroupViewHolder(binding)
    }

    override fun getItemCount(): Int = groupList.size

    override fun onBindViewHolder(holder: MyGroupViewHolder, position: Int) {
        holder.bind(groupList[position])
    }

    fun updateData(groupList: List<GroupViewModel.GroupWithProfile>, username: String) {
        this.groupList = groupList
        this.username = username
        notifyDataSetChanged()
    }

}