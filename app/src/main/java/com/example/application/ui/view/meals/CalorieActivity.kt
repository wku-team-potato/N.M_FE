package com.example.application.ui.view.meals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.databinding.ActivityCalorieBinding
import com.example.application.databinding.ItemCalorieBinding

class CalorieActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCalorieBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUi()
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView.adapter = CalorieAdapter().apply {
            onItemClickListener = {
                startActivity(Intent(this@CalorieActivity, MealsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("title", it.mealTime)
                })
            }
        }
    }

    private class CalorieAdapter : RecyclerView.Adapter<CalorieAdapter.CalorieItemViewHolder>() {
        var onItemClickListener: ((CalorieModel) -> Unit)? = null

        private val items = listOf(
            CalorieModel("아침", 100),
            CalorieModel("점심", 130),
            CalorieModel("저녁", 0)
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalorieItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCalorieBinding.inflate(inflater, parent, false)
            return CalorieItemViewHolder(binding)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: CalorieItemViewHolder, position: Int) {
            val item = items[position]

            with(holder.binding) {
                root.setOnClickListener {
                    onItemClickListener?.invoke(item)
                }

                mealtimeTextView.text = item.mealTime
                calorieTextView.text = "총 칼로리 %dkcal".format(item.calorie)
            }
        }

        class CalorieItemViewHolder(val binding: ItemCalorieBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private data class CalorieModel(
        val mealTime: String,
        val calorie: Int,
    )
}