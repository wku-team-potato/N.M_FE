package com.example.application.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.databinding.FragmentStoreBinding
import com.example.application.databinding.ItemStoreBinding
import com.example.application.ui.store.StoreItemDetailsActivity

class StoreFragment : BaseFragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() = with(binding) {
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_my) {
                showMyPage()
            }

            return@setOnMenuItemClickListener true
        }

        recyclerView.adapter = StoreItemAdapter().apply {
            onItemClickListener = {
                startActivity(
                    Intent(
                        requireContext(),
                        StoreItemDetailsActivity::class.java
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra("data", it)
                    })
            }
        }
    }

    private class StoreItemAdapter : RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder>() {
        var onItemClickListener: ((StoreItemModel) -> Unit)? = null

        private val items = listOf(
            StoreItemModel(R.drawable.img_vita_500, "비타민", 12880),
            StoreItemModel(R.drawable.img_vita_1000, "대박 진짜 비타민", 12880),
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemStoreBinding.inflate(inflater, parent, false)
            return StoreItemViewHolder(binding)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: StoreItemViewHolder, position: Int) {
            val item = items[position]

            with(holder.binding) {
                root.setOnClickListener {
                    onItemClickListener?.invoke(item)
                }

                imageView.setImageResource(item.image)
                nameTextView.text = item.name
                pointTextView.text = "+ %dp".format(item.point)
            }
        }

        class StoreItemViewHolder(val binding: ItemStoreBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    data class StoreItemModel(
        val image: Int,
        val name: String,
        val point: Int,
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readInt()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(image)
            parcel.writeString(name)
            parcel.writeInt(point)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<StoreItemModel> {
            override fun createFromParcel(parcel: Parcel): StoreItemModel {
                return StoreItemModel(parcel)
            }

            override fun newArray(size: Int): Array<StoreItemModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}