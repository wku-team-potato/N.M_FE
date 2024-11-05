package com.example.application.ui.reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.databinding.ActivityRewardBinding
import com.example.application.databinding.ItemRewardBinding

class RewardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRewardBinding.inflate(layoutInflater) }

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
        recyclerView.adapter = RewardAdapter()
    }

    private class RewardAdapter : RecyclerView.Adapter<RewardAdapter.RewardItemViewHolder>() {
        private val items = listOf(
            RewardModel("출석체크", 300, "24일차 출석 완료"),
            RewardModel("출석체크", 300, "23일차 출석 완료"),
            RewardModel("출석체크", 300, "22일차 출석 완료"),
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemRewardBinding.inflate(inflater, parent, false)
            return RewardItemViewHolder(binding)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RewardItemViewHolder, position: Int) {
            val item = items[position]

            with(holder.binding) {
                typeTextView.text = item.type
                pointTextView.text = "+ %dp".format(item.point)
                descriptionTextView.text = item.description
            }
        }

        class RewardItemViewHolder(val binding: ItemRewardBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private data class RewardModel(
        val type: String,
        val point: Int,
        val description: String,
    )
}