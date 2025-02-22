package com.example.application.ui.view.meals

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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

    private var photoUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            Log.d("PhotoPicker", "Selected URI: $uri")
            handleImageSelection(it)
        } ?: Log.d("PhotoPicker", "No media selected")
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri?.let {
                    handleImageSelection(it) // 카메라로 찍은 이미지 업로드 처리
                } ?: Log.e("FoodSearchActivity", "Photo URI is null")
            } else {
                Log.e("FoodSearchActivity", "Camera capture failed")
            }
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
                val intent = Intent(this, MealsActivity::class.java).apply {
                    putExtra("mealType", mealType)
                    putExtra("date", date)
                }
                Log.d("FoodSearchActivity2", "MealType: $mealType, Date: $date")
//                startActivity(intent)
                finish()
//                finish()
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
//            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            try {
//                takePicture.launch(takePictureIntent)
//            } catch (e: ActivityNotFoundException) {
//                Log.e("FoodSearchActivity", "Camera not available", e)
//            }
            val phtoFile = File.createTempFile(
                "temp_photo",
                ".jpg",
                externalCacheDir
            )

            photoUri = FileProvider.getUriForFile(
                this@FoodSearchActivity,
                "${packageName}.provider",
                phtoFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri) // 저장 위치 설정
                putExtra("android.intent.extras.CAMERA_FACING", 0) // 후면 카메라 (0: 후면, 1: 전면)
                putExtra("android.intent.extras.LENS_FACING_BACK", 0) // Android Q 이상 후면 카메라
                putExtra("android.intent.extras.CAMERA_FACING_BACK", true) // 후면 카메라 우선
            }

            takePicture.launch(intent)

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

        foodSearchViewModel.isLoading.observe(this@FoodSearchActivity) { isLoading ->
            if (isLoading) {
                mainContainer.visibility = View.GONE
                llLoading.visibility = View.VISIBLE
            } else {
                llLoading.visibility = View.GONE
                mainContainer.visibility = View.VISIBLE
            }

        }

        foodSearchViewModel.errorMessage.observe(this@FoodSearchActivity) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this@FoodSearchActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
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
            setResult(Activity.RESULT_OK, Intent().apply { putExtra("mealType", mealType) })
            Log.d("FoodSearchActivity", "MealType: $mealType, Date: $date")
//            startActivity(intent)
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
