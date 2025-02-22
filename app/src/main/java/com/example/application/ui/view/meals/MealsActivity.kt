package com.example.application.ui.view.meals

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.databinding.ActivityMealsBinding
import com.example.application.databinding.ItemFoodInMealsBinding
import com.example.application.ui.meals.function.data.MealResponse
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.meals.function.viewmodel.MealViewModel
import com.example.application.ui.meals.function.viewmodel.MealViewModelFactory
import com.example.application.ui.viewmodel.CalorieVieModelFactory
import com.example.application.ui.viewmodel.CalorieViewModel
import com.example.application.utils.RetrofitInstance
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class MealsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMealsBinding.inflate(layoutInflater) }

    private lateinit var mealViewModel: MealViewModel

    private val modifiedList = mutableListOf<MealResponse>()
    private val deletedList = mutableListOf<MealResponse>()

    private var selectedDate: String = ""
    private var mealType: String = ""

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val adapter by lazy {
        MealAdapter(
            onItemModified = { meal, count, quantity ->
                val updatedServingSize = count * quantity
                meal.serving_size = updatedServingSize
                if (!modifiedList.contains(meal)) {
                    modifiedList.add(meal)
                }
            },
            onItemRemoved = { meal ->
                if (!deletedList.contains(meal)) {
                    deletedList.add(meal)
                }
                modifiedList.remove(meal)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val returnMealType = result.data?.getStringExtra("mealType")
                mealType = returnMealType ?: ""
                if (returnMealType.equals("breakfast")){
                        binding.toolbar.title = "아침"
                    } else if (returnMealType.equals("lunch")){
                        binding.toolbar.title = "점심"
                    } else if (returnMealType.equals("dinner")){
                        binding.toolbar.title = "저녁"
                    }

                    Log.d("MealActivity", "Selected date: $selectedDate, MealType: $mealType")
                    observeMealData(mealType)
                    mealViewModel.fetchMealList(selectedDate)
            }
        }

        val redirectToFoodSearch = intent.getBooleanExtra("redirect_to_food_search", false)
        val date = intent.getStringExtra("date")
        if (redirectToFoodSearch) {
            // 2 Depth에 들어오자마자 바로 3 Depth로 이동
//            startActivity(Intent(this, FoodSearchActivity::class.java).apply {
//                putExtra("date", date)
//            })
            launcher.launch(Intent(this, FoodSearchActivity::class.java).apply {
                putExtra("date", date)
            })
        }

        enableEdgeToEdge()
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.md_theme_primary)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val rootView: View = findViewById(android.R.id.content)

        // 하단 컨테이너 뷰 가져오기
        val imageContainer: View = binding.addFoodButton
        val recordContainer: View = binding.doneButton

        // 키보드 상태 디버깅
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // 키보드 열림: 뷰 위치를 키보드 위로 이동
                val offset = -keypadHeight.toFloat()
                Log.d("KeyboardDebug", "Keyboard is OPEN. Moving views up by $offset")

                imageContainer.translationY = offset
                recordContainer.translationY = offset

                Log.d(
                    "ViewDebug",
                    "ImageContainer Y: ${imageContainer.y}, RecordContainer Y: ${recordContainer.y}"
                )
            } else {
                // 키보드 닫힘: 뷰 위치를 원래대로 복원
                Log.d("KeyboardDebug", "Keyboard is CLOSED. Resetting views to original position.")

                imageContainer.translationY = 0f
                recordContainer.translationY = 0f

                Log.d(
                    "ViewDebug",
                    "ImageContainer Y: ${imageContainer.y}, RecordContainer Y: ${recordContainer.y}"
                )
            }
        }

        selectedDate = intent.getStringExtra("date") ?: ""
        mealType = intent.getStringExtra("mealType") ?: ""

        Log.d("MealActivity", "Selected date: $selectedDate, MealType: $mealType")

        val repository = MealRepository(RetrofitInstance.mealService)
        val mealFactory = MealViewModelFactory(repository)
        mealViewModel = ViewModelProvider(this, mealFactory).get(MealViewModel::class.java)

        mealViewModel.fetchMealList(selectedDate)
        mealViewModel.mealList.observe(this) { meals ->
            val filteredMeals = meals.filter { it.meal_type == mealType }
            adapter.setItems(filteredMeals)
        }

        initUi()
        observeMealData(mealType)
        mealViewModel.fetchMealList(selectedDate)
    }

    private val foodSearchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                mealType = intent.getStringExtra("mealType") ?: ""
                Log.d("MealActivity", "Result received, new mealType: $mealType")
                // 여기서 바로 데이터 갱신

                if (mealType.equals("breakfast")){
                    binding.toolbar.title = "아침"
                } else if (mealType.equals("lunch")){
                    binding.toolbar.title = "점심"
                } else if (mealType.equals("dinner")){
                    binding.toolbar.title = "저녁"
                }
                observeMealData(mealType)
                mealViewModel.fetchMealList(selectedDate)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mealViewModel.fetchMealList(selectedDate)
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.title = intent.getStringExtra("title") ?: ""

        recyclerView.layoutManager = LinearLayoutManager(this@MealsActivity)
        recyclerView.adapter = adapter

        addFoodButton.setOnClickListener {
            processChanges_2(selectedDate)
            // 수정 사항 저장 만약 일부 수정을 하고 음식 검색으로 넘어갔다가 다시 돌아오면 수정 사항이 저장되어야 함
            val intent = Intent(this@MealsActivity, FoodSearchActivity::class.java).apply {
                putExtra("date", selectedDate)
                putExtra("mealType", mealType)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            foodSearchLauncher.launch(intent)
        }

        doneButton.setOnClickListener {
            processChanges(selectedDate) // 수정 사항 저장
            Toast.makeText(this@MealsActivity, "수정 사항이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeMealData(mealType: String) {
        mealViewModel.mealList.observe(this) { meals ->
            val filteredMeals = meals.filter { it.meal_type == mealType }

            if (filteredMeals.isEmpty()) {
                binding.recyclerView.visibility = RecyclerView.GONE
                binding.emptyMessage.visibility = RecyclerView.VISIBLE
            } else {
                binding.recyclerView.visibility = RecyclerView.VISIBLE
                binding.emptyMessage.visibility = RecyclerView.GONE
                adapter.setItems(filteredMeals)
            }
        }
    }

    private fun processChanges_2(date: String) {
        lifecycleScope.launch {
            try {
                modifiedList.forEach { meal ->
                    Log.d("MealActivity", "Updating meal: $meal")
                    mealViewModel.updateMeal(
                        id = meal.id,
                        foodId = meal.food_id,
                        mealType = meal.meal_type,
                        servingSize = meal.serving_size,
                        date = date
                    )
                }

                deletedList.forEach { meal ->
                    Log.d("MealActivity", "Deleting meal: $meal")
                    mealViewModel.deleteMeal(meal.id)
                }
            } catch (e: Exception) {
                Log.e("MealActivity", "Error processing changes: ${e.message}")
            }
        }
    }

    private fun processChanges(date: String) {
        lifecycleScope.launch {
            try {
                modifiedList.forEach { meal ->
                    Log.d("MealActivity", "Updating meal: $meal")
                    mealViewModel.updateMeal(
                        id = meal.id,
                        foodId = meal.food_id,
                        mealType = meal.meal_type,
                        servingSize = meal.serving_size,
                        date = date
                    )
                }

                deletedList.forEach { meal ->
                    Log.d("MealActivity", "Deleting meal: $meal")
                    mealViewModel.deleteMeal(meal.id)
                }
                Toast.makeText(this@MealsActivity, "수정 사항이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MealsActivity, "변경 사항을 저장하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("MealActivity", "Error processing changes: ${e.message}")
            }
        }
    }
}