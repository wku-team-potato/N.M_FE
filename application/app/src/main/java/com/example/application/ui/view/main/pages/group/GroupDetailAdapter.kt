package com.example.application.ui.view.main.pages.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.data.model.groups.response.GroupMemberResponse
import com.example.application.databinding.ItemGroupMemberBinding
import com.example.application.ui.viewmodel.main.group.AdapterData

class GroupDetailAdapter(
    private val adapterData: MutableList<AdapterData>
) : RecyclerView.Adapter<GroupDetailAdapter.GroupDetailViewHolder>() {

    inner class GroupDetailViewHolder(private val binding: ItemGroupMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: AdapterData) {
            binding.tvUserName.text = adapterData.username
            if (adapterData.is_public) {
                binding.tvUserPublic.text = "정보 공개"
                binding.tvUserHeight.text = adapterData.userHeight.toString()
                binding.tvUserWeight.text = adapterData.userWeight.toString()
            } else {
                binding.tvUserPublic.text = "정보 비공개"
                binding.userInfoContainer.visibility = ViewGroup.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupDetailViewHolder {
        val binding = ItemGroupMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupDetailViewHolder, position: Int) {
        holder.bind(adapterData[position])
    }

    override fun getItemCount(): Int {
        return adapterData.size
    }

    fun updateData(newData: MutableList<AdapterData>) {
        adapterData.clear()
        adapterData.addAll(newData)
        notifyDataSetChanged()
    }

}