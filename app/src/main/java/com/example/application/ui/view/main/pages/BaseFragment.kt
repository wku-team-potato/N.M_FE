package com.example.application.ui.view.main.pages

import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.application.utils.RetrofitInstance
import com.example.application.databinding.DialogMyPageBinding
import com.example.application.data.model.response.ProfileResponse
import com.example.application.data.repository.ProfileRepository
import com.example.application.ui.viewmodel.ProfileViewModel
import com.example.application.ui.viewmodel.ProfileViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class BaseFragment : Fragment() {
    private lateinit var profileViewModel : ProfileViewModel

    fun showMyPage() {
        val binding = DialogMyPageBinding.inflate(LayoutInflater.from(requireContext()))

        initViewModel(binding)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .show()

        with(binding) {
            editButton.setOnClickListener {
                editButton.isVisible = false
                doneButton.isVisible = true
                nicknameEditText.isEnabled = true
                heightEditText.isEnabled = true
                weightEditText.isEnabled = true
            }

            doneButton.setOnClickListener {
                nicknameEditText.isEnabled = false
                heightEditText.isEnabled = false
                weightEditText.isEnabled = false

                val updatedProfile = ProfileResponse(
                    username = nicknameEditText.text.toString(),
                    height = heightEditText.text.toString().toFloat(),
                    weight = weightEditText.text.toString().toFloat()
                )

                profileViewModel.updateProfileInfo(updatedProfile)

                profileViewModel.updateResult.observe(viewLifecycleOwner) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "프로필 수정 완료", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "프로필 수정 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                dialog.dismiss()
            }
        }
    }

    private fun initViewModel(binding : DialogMyPageBinding){
        val repository = ProfileRepository(RetrofitInstance.profileService)
        val factory = ProfileViewModelFactory(repository)
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        profileViewModel.latestRecord.observe(viewLifecycleOwner) { record ->
            record?.let {
                binding.nicknameEditText.setText(it.username)
                val formattedHeight = if (it.height % 1f == 0f) it.height.toInt().toString() else it.height.toString()
                val formattedWeight = if (it.weight % 1f == 0f) it.weight.toInt().toString() else it.weight.toString()

                binding.heightEditText.setText(formattedHeight)
                binding.weightEditText.setText(formattedWeight)
            }
        }

        profileViewModel.loadLatestRecord()
    }
}