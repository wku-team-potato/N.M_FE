package com.example.application.ui.main.pages

import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.application.RetrofitInstance
import com.example.application.databinding.DialogMyPageBinding
import com.example.application.ui.profile.function.data.ProfileResponse
import com.example.application.ui.profile.function.repository.ProfileRepository
import com.example.application.ui.profile.function.viewmodel.ProfileViewModel
import com.example.application.ui.profile.function.viewmodel.ProfileViewModelFactory
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
                idEditText.isEnabled = true
                heightEditText.isEnabled = true
                weightEditText.isEnabled = true
            }

            doneButton.setOnClickListener {
                idEditText.isEnabled = false
                heightEditText.isEnabled = false
                weightEditText.isEnabled = false

                val updatedProfile = ProfileResponse(
                    user_id = idEditText.text.toString(),
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
                binding.idEditText.setText(it.user_id)
                binding.heightEditText.setText("${it.height}")
                binding.weightEditText.setText("${it.weight}")
            }
        }

        profileViewModel.loadLatestRecord()
    }
}