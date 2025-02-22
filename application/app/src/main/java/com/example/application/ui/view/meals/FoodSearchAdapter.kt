package com.example.application.ui.view.meals

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.ItemFoodInSearchBinding
import com.example.application.ui.meals.function.data.FoodResponse

class FoodSearchAdapter(private val onFoodSelected: (FoodResponse?, Boolean) -> Unit) :
    RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder>() {

    private val foodList = mutableListOf<FoodResponse>() // 전체 리스트
    private val selectedFoods = mutableSetOf<FoodResponse>() // 선택된 항목 저장

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodInSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        val isSelected = selectedFoods.contains(food)
        holder.bind(food, isSelected) { clickedFood ->
            // 선택 상태 토글
            if (selectedFoods.contains(clickedFood)) {
                selectedFoods.remove(clickedFood)
                onFoodSelected(clickedFood, false) // 선택 해제
            } else {
                selectedFoods.add(clickedFood)
                onFoodSelected(clickedFood, true) // 선택
            }
            notifyItemChanged(position) // 해당 항목만 업데이트
        }
    }

    override fun getItemCount(): Int = foodList.size

    // 새로운 데이터 리스트 전달
    fun submitList(newList: List<FoodResponse>) {
        foodList.clear()
        foodList.addAll(newList)
        notifyDataSetChanged()
    }

    // 선택된 항목 업데이트
    fun updateSelectedFoods(selectedFoods: List<FoodResponse>) {
        this.selectedFoods.clear()
        this.selectedFoods.addAll(selectedFoods)
        notifyDataSetChanged()
    }

    class FoodViewHolder(private val binding: ItemFoodInSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: FoodResponse, isSelected: Boolean, onClick: (FoodResponse) -> Unit) {
            binding.foodNameTextView.text = food.food_name
            // 선택된 상태 강조 표시
            binding.root.setBackgroundColor(
                if (isSelected) Color.LTGRAY else Color.TRANSPARENT
            )
            binding.root.setOnClickListener { onClick(food) }
        }
    }
}
