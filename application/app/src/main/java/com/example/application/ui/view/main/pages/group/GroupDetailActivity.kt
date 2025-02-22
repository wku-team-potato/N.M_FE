package com.example.application.ui.view.main.pages.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.application.R
import com.example.application.data.model.groups.request.GroupPublicRequest
import com.example.application.data.repository.GroupMyRepository
import com.example.application.data.repository.ProfileRepository
import com.example.application.databinding.ActivityGroupDetailBinding
import com.example.application.ui.viewmodel.main.group.AdapterData
import com.example.application.ui.viewmodel.main.group.GroupDetailViewModel
import com.example.application.ui.viewmodel.main.group.GroupDetailViewModelFactory
import com.example.application.utils.RetrofitInstance

class GroupDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityGroupDetailBinding.inflate(layoutInflater) }

    private lateinit var groupDetailViewModel: GroupDetailViewModel

    private lateinit var adapter: GroupDetailAdapter

    private var isUpdatingToggle = false

    companion object {
        private const val EXTRA_GROUP_ID = "extra_group_id"

        // Intent 생성 함수
        fun createIntent(context: Context, groupId: Int): Intent {
            return Intent(context, GroupDetailActivity::class.java).apply {
                putExtra(EXTRA_GROUP_ID, groupId)
            }
        }
    }

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

        val groupRepository = GroupMyRepository(RetrofitInstance.groupMyService)
        val profileRepository = ProfileRepository(RetrofitInstance.profileService)
        val factory = GroupDetailViewModelFactory(groupRepository, profileRepository)
        groupDetailViewModel = ViewModelProvider(this, factory).get(GroupDetailViewModel::class.java)

        groupDetailViewModel.loadGroupInfo(intent.getIntExtra(EXTRA_GROUP_ID, -1))

        groupDetailViewModel.loadMyGroupInfo(intent.getIntExtra(EXTRA_GROUP_ID, -1))

        groupDetailViewModel.groupInfo.observe(this) {
            if (it != null) {
                binding.tvGroupNameValue.text = it.name
                binding.tvGroupDescriptionValue.text = it.description
//                binding.tvGroupDate.text = it.created_at
            } else {
                Log.d("GroupDetailActivity___", "null")
            }
        }

        groupDetailViewModel.memberList.observe(this) {
            if (it != null) {
                binding.tvGroupMemberCount.text = it.size.toString() + "명"
            } else {
                Log.d("GroupDetailActivity___", "null")
            }
        }

        groupDetailViewModel.myInfo.observe(this) {
            if (it != null) {

                Log.d("GroupDetailActivity___", it.toString())

            } else {
                Log.d("GroupDetailActivity___", "null")
            }
        }

        groupDetailViewModel.isPublic.observe(this) { isPublic ->
            Log.d("GroupDetailActivity", "Observed isPublic: $isPublic")


            if (!isUpdatingToggle) {
                binding.tbGroupPublic.isChecked = isPublic
            }
        }

        adapter = GroupDetailAdapter(mutableListOf())
        binding.recyclerView.adapter = adapter

        groupDetailViewModel.adapterData.observe(this) { adapterData ->
            Log.d("GroupDetailActivity", "Adapter data: $adapterData")
            adapter.updateData(adapterData) // 새로운 데이터로 어댑터 갱신
        }

        binding.tbGroupPublic.setOnClickListener {
            if (!isUpdatingToggle) { // 중복 호출 방지
                val groupId = intent.getIntExtra(EXTRA_GROUP_ID, -1)
                val isPublic = binding.tbGroupPublic.isChecked

                // 상태 변경 알림
                Log.d("GroupDetailActivity", "Toggle clicked: $isPublic")

                // ViewModel에 요청 보내기
                isUpdatingToggle = true // 상태 업데이트 플래그 활성화
                groupDetailViewModel.togglePublic(groupId, isPublic)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        Log.d("GroupDetailActivity", intent.getIntExtra(EXTRA_GROUP_ID, -1).toString())
//        Toast.makeText(this, intent.getIntExtra(EXTRA_GROUP_ID, -1).toString(), Toast.LENGTH_SHORT).show()
    }
}