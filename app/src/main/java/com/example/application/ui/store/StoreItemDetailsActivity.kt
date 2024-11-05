package com.example.application.ui.store

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.application.R
import com.example.application.databinding.ActivityStoreItemDetailsBinding
import com.example.application.ui.main.pages.StoreFragment

class StoreItemDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStoreItemDetailsBinding.inflate(layoutInflater) }

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

        val data: StoreFragment.StoreItemModel? = intent.getParcelableExtra("data")
        if (data != null) {
            imageView.setImageResource(data.image)
            nameTextView.text = data.name
            pointTextView.text = "+ %dp".format(data.point)
        }
    }
}