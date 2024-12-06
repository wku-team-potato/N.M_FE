package com.example.application.ui.view.meals

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.databinding.ItemCalorieBinding
import com.example.application.ui.meals.function.data.MealDetailResponse

class CalorieAdapter(
    private val mealList: List<String>,
    private val calorieData: List<Int>,
    private val nutrientData: List<MealDetailResponse>
) : RecyclerView.Adapter<CalorieAdapter.MealViewHolder>() {

    var onItemClickListener: ((String) -> Unit)? = null

    class MealViewHolder(
        private val binding: ItemCalorieBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            mealType: String,
            calorie: Int,
            nutrients: MealDetailResponse,
            onClick: ((String) -> Unit)?
        ) {
            binding.mealtimeTextView.text = mealType
            binding.calorieText.text = "총 칼로리 ${calorie}kcal"

            val baseCarbohydrate = 300
            val baseProtein = 60
            val baseFat = 60

            val carbohydratePercent = (nutrients.carbohydrate / baseCarbohydrate * 100).toInt()
            val proteinPercent = (nutrients.protein / baseProtein * 100).toInt()
            val fatPercent = (nutrients.fat / baseFat * 100).toInt()

            // 탄수화물
            updateNutrientUI(
                binding.carboText,
                binding.carboProgress,
                nutrients.carbohydrate.toInt(),
                baseCarbohydrate,
                carbohydratePercent,
                R.color.md_theme_primary,
                R.color.md_theme_error
            )

            // 단백질
            updateNutrientUI(
                binding.proteinText,
                binding.proteinProgress,
                nutrients.protein.toInt(),
                baseProtein,
                proteinPercent,
                R.color.md_theme_primary,
                R.color.md_theme_error
            )

            // 지방
            updateNutrientUI(
                binding.fatText,
                binding.fatProgress,
                nutrients.fat.toInt(),
                baseFat,
                fatPercent,
                R.color.md_theme_primary,
                R.color.md_theme_error
            )

            binding.root.setOnClickListener { onClick?.invoke(mealType) }
        }

        private fun updateNutrientUI(
            textView: TextView,
            progressBar: ProgressBar,
            current: Int,
            base: Int,
            percent: Int,
            normalColor: Int,
            overLimitColor: Int
        ) {
            textView.text = "$current / $base ($percent%)"
            textView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (percent > 100) overLimitColor else normalColor
                )
            )


            progressBar.progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (percent > 100) overLimitColor else normalColor
                )
            )

            val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, percent)
            animator.duration = 1000
            animator.interpolator = LinearInterpolator()
            animator.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemCalorieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(mealList[position], calorieData[position], nutrientData[position], onItemClickListener)
    }

    override fun getItemCount() = mealList.size
}