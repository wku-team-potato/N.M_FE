package com.example.application.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.application.R
import com.example.application.databinding.ItemStoreBinding
import com.example.application.ui.store.functions.data.StoreResponse
import java.net.URLDecoder

// item 클릭 시 상세페이지로 이동
class StoreAdapter(private val onItemClick: (StoreResponse) -> Unit) :
    RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    // RecyclerView에 표시할 데이터 리스트를 저장하는 변수
    private val items = mutableListOf<StoreResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding)
    }

    // 각 ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size

    // 데이터 변경 RecyclerView 갱신
    fun updateItems(newItems: List<StoreResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class StoreViewHolder(private val binding: ItemStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreResponse, onItemClick: (StoreResponse) -> Unit) = with(binding) {
            nameTextView.text = item.name
            pointTextView.text = "${item.price}p"

            // 상품 이미지 로드
            val correctedUrl = item.img.substringAfter("8000/")
            val decodedUrl = URLDecoder.decode(correctedUrl, "UTF-8")

            Glide.with(imageView.context)
                .load(decodedUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)

            root.setOnClickListener { onItemClick(item) }
        }
    }
}