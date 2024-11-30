package com.example.application.ui.meals

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.RetrofitInstance
import com.example.application.databinding.ActivityMealsBinding
import com.example.application.databinding.ItemFoodInMealsBinding
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.meals.function.viewmodel.MealViewModel
import com.example.application.ui.meals.function.viewmodel.MealViewModelFactory
import kotlin.math.max
import kotlin.math.min

class MealActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMealsBinding.inflate(layoutInflater) }
    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory(MealRepository(RetrofitInstance.mealService))
    }

    private val adapter by lazy {
        MealAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val selectedDate = intent.getStringExtra("date") ?: ""
        val mealType = intent.getStringExtra("mealType") ?: ""

        Log.d("MealActivity", "Selected date: $selectedDate, MealType: $mealType")

        initUi()
        observeMealData(mealType)
        fetchMealList()
    }


    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.title = intent.getStringExtra("title") ?: ""

        recyclerView.layoutManager = LinearLayoutManager(this@MealActivity)
        recyclerView.adapter = adapter

        addFoodButton.setOnClickListener {
            startActivity(Intent(this@MealActivity, FoodSearchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }

        doneButton.setOnClickListener {
            finish()
        }
    }

    private fun observeMealData(mealType: String) {
        viewModel.mealList.observe(this) { meals ->
            val filteredMeals = meals.filter { it.meal_type == mealType }

            if (filteredMeals.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyMessage.visibility = View.GONE
                adapter.submitList(filteredMeals)
            }
        }
    }

    private fun fetchMealList() {
        val selectedDate = intent.getStringExtra("date") ?: ""
        Log.d("MealActivity", "Selected date: $selectedDate")
        if (selectedDate.isNotEmpty()) {
            viewModel.fetchMealList(selectedDate)
        } else {
            Toast.makeText(this, "Invalid date.", Toast.LENGTH_SHORT).show()
        }
    }
}