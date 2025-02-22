package com.example.application.ui.view.store

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.application.R
import com.example.application.utils.RetrofitInstance
import com.example.application.databinding.ActivityStoreItemDetailsBinding
import com.example.application.data.model.response.StoreResponse
import com.example.application.data.repository.ProfilePointRepository
import com.example.application.data.repository.StoreRepository
import com.example.application.ui.viewmodel.StoreViewModel
import com.example.application.ui.viewmodel.StoreViewModelFactory
import java.net.URLDecoder

class StoreItemDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStoreItemDetailsBinding.inflate(layoutInflater) }
    private lateinit var storeViewModel: StoreViewModel

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

        initViewModel()
        initUi()
    }

    private fun initViewModel() {
        val storeRepository = StoreRepository(RetrofitInstance.storeService)
        val profilePointRepository = ProfilePointRepository(RetrofitInstance.profilePointService)
        val factory = StoreViewModelFactory(storeRepository, profilePointRepository)
        storeViewModel = ViewModelProvider(this, factory).get(StoreViewModel::class.java)

        storeViewModel.buyResult.observe(this) { message ->
            message?.let {
                // 구매 결과 메시지를 표시
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                if (message == "구매 성공!") {
                    setResult(RESULT_OK) // 구매 성공 시 RESULT_OK를 설정하여 부모 액티비티에 알림
                    finish()
                }

                // 메시지를 보여준 후 초기화
                storeViewModel.resetBuyResult()
            }
        }
    }

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        val data = intent.getSerializableExtra("data") as? StoreResponse // Intent을 통해 전달받은 상품 데이터 로드
        if (data != null) {

            // 이미지 로드
            val correctedUrl = data.img.substringAfter("8000/")
            val decodedUrl = URLDecoder.decode(correctedUrl, "UTF-8")

            Glide.with(imageView.context)
                .load(decodedUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)

            nameTextView.text = data.name
            pointTextView.text = "${data.price}p"
            descriptionTextView.text = data.description

            purchaseButton.setOnClickListener {
                Log.d("StoreItemDetails", "Purchase button clicked")

                val data = intent.getSerializableExtra("data") as? StoreResponse
                if (data != null) {
                    Log.d("StoreItemDetails", "Item ID: ${data.id}")
                    storeViewModel.buyItem(data.id)
                } else {
                    Log.e("StoreItemDetails", "No data found for purchase")
                }
            }

        }
    }
}