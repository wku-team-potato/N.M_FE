package com.example.application.ui.view.main.pages.group

import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.data.model.groups.response.GroupResponse
import com.example.application.databinding.FragmentMyGroupBinding
import com.example.application.ui.view.main.pages.GroupFragment
import com.example.application.ui.viewmodel.main.group.GroupViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MyGroupFragment : Fragment() {
    private var _binding: FragmentMyGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MyGroupAdapter
    private val viewModel: GroupViewModel by lazy {
        (requireParentFragment() as GroupFragment).getSharedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyGroupBinding.inflate(inflater, container, false)

        adapter = MyGroupAdapter(
            emptyList(), // groupList 초기화
            "",          // username 초기화
            { group ->
                // onDetailClick 리스너
                onDetailGroup(group)
            },
            { group ->
                // onDeleteClick 리스너
                deleteGroup(group)
            }
        )

        binding.recyclerViewMyGroup.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyGroup.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            handleLoadingState(isLoading)
        }

        viewModel.myGroups.observe(viewLifecycleOwner) { groups ->
            Log.d("MyGroupFragment", "groups: $groups")
            if (groups.isEmpty()) {
                binding.recyclerViewMyGroup.visibility = View.GONE
                binding.tvEmptyGroupMessage.visibility = View.VISIBLE
                adapter.updateData(emptyList(), viewModel.myInfo.value!!.username)
            } else {
                binding.recyclerViewMyGroup.visibility = View.VISIBLE
                binding.tvEmptyGroupMessage.visibility = View.GONE
                adapter.updateData(groups, viewModel.myInfo.value!!.username)
            }
        }

        binding.fabCreateGroup.setOnClickListener {
            showCreateGroupDialog()
        }

        return binding.root
    }

    private fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.llLoading.visibility = View.VISIBLE
            binding.fabCreateGroup.visibility = View.GONE
            binding.recyclerViewMyGroup.visibility = View.GONE
        } else {
            binding.llLoading.visibility = View.GONE
            binding.fabCreateGroup.visibility = View.VISIBLE
            binding.recyclerViewMyGroup.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
//        binding.recyclerViewMyGroup.visibility = View.GONE
//        binding.fabCreateGroup.visibility = View.GONE
//        binding.tvEmptyGroupMessage.visibility = View.GONE
//        binding.lottieLoading.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun showCreateGroupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_group, null)
        val groupNameEditText = dialogView.findViewById<TextInputEditText>(R.id.edit_group_name)
        val groupDescriptionEditText = dialogView.findViewById<TextInputEditText>(R.id.edit_group_description)
        val closeButton = dialogView.findViewById<Button>(R.id.btn_close_dialog)
        val createButton = dialogView.findViewById<Button>(R.id.btn_create_group)

        // 다이얼로그 생성
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // 둥근 모서리 배경 설정
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background)

        // 닫기 버튼 이벤트
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // 그룹 생성 버튼 이벤트
        createButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString()
            val groupDescription = groupDescriptionEditText.text.toString()

            if (groupName.isBlank() || groupDescription.isBlank()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.createGroup(groupName, groupDescription)

                viewModel.createGroupMessage.observe(viewLifecycleOwner) { message ->
                    if (message.isNotBlank()) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun deleteGroup(group: GroupResponse) {
        // onDeleteClick 리스너
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_group, null)

        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // 둥근 모서리 배경 설정
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background)


        // 레이아웃 내부의 버튼 가져오기
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)

        // 제목과 메시지 동적으로 설정 가능
        dialogTitle.text = "삭제 확인"
        dialogMessage.text = "${group.name}을(를) 삭제하시겠습니까?"

        // 버튼 클릭 리스너 설정
        cancelButton.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }
        confirmButton.setOnClickListener {
//            viewModel.deleteGroup(group.id)
            viewModel.leaveGroup(group.id)
            dialog.dismiss() // 다이얼로그 닫기
//                    deleteGroup(group) // 실제 삭제 처리
        }

        // 다이얼로그 표시
        dialog.show()
    }

    private fun onDetailGroup(group: GroupResponse) {
        val intent = GroupDetailActivity.createIntent(requireContext(), group.id)
        startActivity(intent)
    }
}