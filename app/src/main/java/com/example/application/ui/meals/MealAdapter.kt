package com.example.application.ui.meals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.ItemFoodInMealsBinding
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.viewmodel.MealViewModel

class MealAdapter : ListAdapter<MealResponse, MealAdapter.MealViewHolder>(MealDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodInMealsBinding.inflate(inflater, parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class MealViewHolder(private val binding: ItemFoodInMealsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: MealResponse) {
            binding.nameTextView.text = meal.food.food_name
            binding.quantityEditText.setText("${meal.serving_size}")
        }
    }

    // 기존 리스트와 비교 후 변경된 항목만 생성
    class MealDiffCallback : DiffUtil.ItemCallback<MealResponse>() {
        override fun areItemsTheSame(oldItem: MealResponse, newItem: MealResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MealResponse, newItem: MealResponse): Boolean {
            return oldItem == newItem
        }
    }
}