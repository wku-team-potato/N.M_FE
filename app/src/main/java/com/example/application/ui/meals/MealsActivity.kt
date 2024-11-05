package com.example.application.ui.meals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.databinding.ActivityMealsBinding
import com.example.application.databinding.ItemFoodInMealsBinding
import kotlin.math.max
import kotlin.math.min

class MealsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMealsBinding.inflate(layoutInflater) }

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
        toolbar.title = intent.getStringExtra("title") ?: ""

        recyclerView.adapter = MealsAdapter()

        addFoodButton.setOnClickListener {
            startActivity(Intent(this@MealsActivity, FoodSearchActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }

        doneButton.setOnClickListener { finish() }
    }

    private class MealsAdapter : RecyclerView.Adapter<MealsAdapter.FoodItemViewHolder>() {
        private val items = mutableListOf(
            FoodModel("흰쌀밥", 1, 100),
            FoodModel("계란", 1, 100),
            FoodModel("김치", 1, 100),
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemFoodInMealsBinding.inflate(inflater, parent, false)
            binding.quantityEditText.transformationMethod = null

            return FoodItemViewHolder(binding)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
            val item = items[position]

            with(holder.binding) {
                nameTextView.text = item.name
                countTextView.text = "${item.count}"
                quantityEditText.setText("${item.quantity}")

                minusButton.setOnClickListener {
                    holder.bindingAdapterPosition.takeIf { it >= 0 }?.let { p ->
                        items[p] = items[p].copy(count = max(items[p].count - 1, 1))
                        countTextView.text = "${items[p].count}"
                    }
                }

                plusButton.setOnClickListener {
                    holder.bindingAdapterPosition.takeIf { it >= 0 }?.let { p ->
                        items[p] = items[p].copy(count = min(items[p].count + 1, 99))
                        countTextView.text = "${items[p].count}"
                    }
                }

                quantityEditText.doAfterTextChanged {
                    holder.bindingAdapterPosition.takeIf { it >= 0 }?.let { p ->
                        quantityEditText.text.toString().toIntOrNull()?.let {
                            items[p] = items[p].copy(quantity = it)
                        }
                    }
                }

                removeButton.setOnClickListener {
                    holder.bindingAdapterPosition.takeIf { it >= 0 }?.let { p ->
                        items.removeAt(p)
                        notifyItemRemoved(p)
                    }
                }
            }
        }

        class FoodItemViewHolder(val binding: ItemFoodInMealsBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private data class FoodModel(
        val name: String,
        val count: Int,
        val quantity: Int,
    )
}