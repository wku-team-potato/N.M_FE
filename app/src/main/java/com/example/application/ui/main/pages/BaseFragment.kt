package com.example.application.ui.main.pages

import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.application.databinding.DialogMyPageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseFragment : Fragment() {
    fun showMyPage() {
        val binding = DialogMyPageBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .show()

        with(binding) {
            editButton.setOnClickListener {
                editButton.isVisible = false
                doneButton.isVisible = true
            }

            doneButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}