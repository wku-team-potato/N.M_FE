package com.example.application.ui.view.meals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.R
import com.example.application.databinding.ActivityCalorieBinding
import com.example.application.ui.meals.function.data.MealDetailResponse
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.meals.function.viewmodel.MealViewModel
import com.example.application.ui.meals.function.viewmodel.MealViewModelFactory
import com.example.application.ui.viewmodel.CalorieVieModelFactory
import com.example.application.ui.viewmodel.CalorieViewModel
import com.example.application.utils.RetrofitInstance

class CalorieActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCalorieBinding.inflate(layoutInflater) }
//    private lateinit var mealViewModel: MealViewModel
    private lateinit var calorieViewModel: CalorieViewModel
    private lateinit var selectedDate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_primary)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val repository = MealRepository(RetrofitInstance.mealService)
//        val factory = MealViewModelFactory(repository)
//        mealViewModel = ViewModelProvider(this, factory).get(MealViewModel::class.java)

        val calorieFactory = CalorieVieModelFactory(repository)
        calorieViewModel = ViewModelProvider(this, calorieFactory).get(CalorieViewModel::class.java)

        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        Log.d("CalorieActivity", "Received date for API : $selectedDate")

        initUi()
        fetchMealSummary()
    }

    override fun onResume() {
        super.onResume()
        Log.d("CalorieActivity", "onResume called, refreshing data for date: $selectedDate")
        fetchMealSummary()
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView.layoutManager = LinearLayoutManager(this@CalorieActivity)
    }

    private fun fetchMealSummary() {
        Log.d("CalorieActivity", "Fetching data for date: $selectedDate")

        calorieViewModel.mealSummary.observe(this) { mealSummary ->
            val mealList = listOf("아침", "점심", "저녁")
            val calorieData = listOf(
                mealSummary.breakfast.calorie.toIntOrZero(),
                mealSummary.lunch.calorie.toIntOrZero(),
                mealSummary.dinner.calorie.toIntOrZero()
            )
            val nutrientData = listOf(
                mealSummary.breakfast,
                mealSummary.lunch,
                mealSummary.dinner
            )
            Log.d("CalorieActivity", "Calorie data: $calorieData")
            updateAdapter(mealList, calorieData, nutrientData)
        }

        // ViewModel에 API 요청 전달
        calorieViewModel.getMealSummary(selectedDate)
    }

    private fun updateAdapter(mealList: List<String>, calorieData: List<Int>, nutrientData: List<MealDetailResponse>) {
        binding.recyclerView.adapter = CalorieAdapter(mealList, calorieData, nutrientData).apply {
            onItemClickListener = { mealTime ->
                Log.d("CalorieActivity", "Clicked on: $mealTime")

                val mealType = when (mealTime){
                    "아침" -> "breakfast"
                    "점심" -> "lunch"
                    "저녁" -> "dinner"
                    else -> ""
                }

                val intent = Intent(this@CalorieActivity, MealsActivity::class.java).apply {
                    putExtra("title", mealTime)
                    putExtra("date", selectedDate)
                    putExtra("mealType", mealType)
                }
                startActivity(intent)
            }
        }
    }

    // 확장 함수: Double -> Int 변환, null 또는 빈 값 처리
    private fun Double?.toIntOrZero(): Int = this?.toInt() ?: 0
}