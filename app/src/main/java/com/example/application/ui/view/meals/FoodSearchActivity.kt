package com.example.application.ui.view.meals

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.R
import com.example.application.data.repository.DetectionRepository
import com.example.application.data.repository.FoodRepository
import com.example.application.databinding.ActivityFoodSearchBinding
import com.example.application.ui.meals.function.data.FoodResponse
import com.example.application.ui.meals.function.repository.MealRepository
import com.example.application.ui.viewmodel.FoodSearchViewModel
import com.example.application.ui.viewmodel.FoodSearchViewModelFactory
import com.example.application.utils.ImageHelper
import com.example.application.utils.RetrofitInstance
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class FoodSearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFoodSearchBinding.inflate(layoutInflater) }
    private lateinit var foodSearchViewModel: FoodSearchViewModel

    private var selectedFoods = mutableListOf<FoodResponse>()
    private var mealType: String = ""
    private var date: String = ""

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            Log.d("PhotoPicker", "Selected URI: $uri")
            handleImageSelection(it)
        } ?: Log.d("PhotoPicker", "No media selected")
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val photoUri: Uri? = result.data?.data
            photoUri?.let {
                Log.d("PhotoPicker", "Captured URI: $it")
                handleImageSelection(it)
            } ?: Log.e("PhotoPicker", "No photo captured")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        date = intent.getStringExtra("date").orEmpty()
        mealType = intent.getStringExtra("mealType").orEmpty()

        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
        val factory = FoodSearchViewModelFactory(
            FoodRepository(RetrofitInstance.foodService),
            MealRepository(RetrofitInstance.mealService),
            DetectionRepository(RetrofitInstance.detectionService)
        )
        foodSearchViewModel = factory.create(FoodSearchViewModel::class.java)

        foodSearchViewModel.operationResult.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            if (message.contains("추가")) {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra("mealType", mealType) })
                finish()
            }
        }

        foodSearchViewModel.detectionResult.observe(this) { results ->
            results?.result?.forEach {
                Log.d("FoodSearchActivity", "Detected food: ${it.label}, confidence: ${it.confidence}")
            }
        }
    }

    private fun setupUI() = with(binding) {
        backButton.setOnClickListener { finish() }

//        handleKeyboardVisibility(findViewById(android.R.id.content), imageContainer, recordContainer)

        galleryButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        cameraButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                takePicture.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e("FoodSearchActivity", "Camera not available", e)
            }
        }

        mealTypeSpinner.onItemSelectedListener = createMealTypeListener()
        mealTypeSpinner.setSelection(getMealTypePosition(mealType))

        recyclerView.layoutManager = LinearLayoutManager(this@FoodSearchActivity)
        val foodAdapter = FoodSearchAdapter { food, isSelected ->
            if (isSelected) {
                food?.let {
                    foodSearchViewModel.toggleFood(it)
                }
            } else {
                showToast("이미 선택된 음식이 있습니다.")
            }
        }

        foodSearchViewModel.selectedFoods.observe(this@FoodSearchActivity) { selectedFoods ->
            foodAdapter.submitList(selectedFoods)
            foodAdapter.updateSelectedFoods(selectedFoods)
        }

        recyclerView.adapter = foodAdapter


        searchEditText.doAfterTextChanged {
            val query = it.toString().trim()
            toggleContainers(query)
            if (query.isNotEmpty()) foodSearchViewModel.fetchFoods(query)

            Log.d("FoodSearchActivity", "Search query: $query")

            foodSearchViewModel.foodList.observe(this@FoodSearchActivity) {foodList ->
                Log.d("FoodSearchActivity__", "Fetched food list: $foodList")
                foodAdapter.submitList(foodList)
            }
        }

        recordButton.setOnClickListener { validateAndSubmit() }
    }

    private fun handleImageSelection(uri: Uri) {
        val resizedFile = ImageHelper.resizeImageFromUri(contentResolver, uri, 640, 640)
        resizedFile?.let {
            Log.d("FoodSearchActivity__", "Resized file: ${it.name}, Size: ${mealType}, ${date} bytes")
            foodSearchViewModel.uploadImage(it.absolutePath, mealType, date)
            Log.d("FoodSearchActivity", "Resized file path: ${it.absolutePath}")
        } ?: Log.e("FoodSearchActivity", "Failed to resize image")
    }

    private fun validateAndSubmit() {
        val mealType = mealType

        if (mealType == "") {
            showToast("식사 시간을 선택하세요.")
            return
        }

        foodSearchViewModel.addSelectedFoods(mealType, date) {
            // 성공 시 MealActivity로 이동
            val intent = Intent(this, MealsActivity::class.java).apply {
                putExtra("mealType", mealType)
                putExtra("date", date)
            }
            Log.d("FoodSearchActivity", "MealType: $mealType, Date: $date")
            startActivity(intent)
            finish()
        }
        Log.d("FoodSearchActivity", "Submitting mealType: $mealType with foods: ${foodSearchViewModel.selectedFoods.value}")
    }

    private fun createMealTypeListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            mealType = when (position) {
                0 -> "breakfast"
                1 -> "lunch"
                2 -> "dinner"
                else -> ""
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            mealType = ""
        }
    }

    private fun getMealTypePosition(mealType: String) = when (mealType) {
        "breakfast" -> 0
        "lunch" -> 1
        "dinner" -> 2
        else -> 0
    }

    private fun toggleContainers(query: String) = with(binding) {
        imageContainer.isVisible = query.isBlank()
        recordButton.isVisible = query.isNotBlank()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
