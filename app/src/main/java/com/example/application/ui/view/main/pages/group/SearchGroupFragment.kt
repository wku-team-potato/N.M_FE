package com.example.application.ui.view.main.pages.group

import SearchGroupAdapter
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.R
import com.example.application.databinding.FragmentMyGroupBinding
import com.example.application.databinding.FragmentSearchGroupBinding
import com.example.application.ui.view.main.pages.GroupFragment
import com.example.application.ui.viewmodel.main.group.GroupViewModel

class SearchGroupFragment : Fragment() {
    private var _binding: FragmentSearchGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchGroupAdapter
    private val viewModel: GroupViewModel by lazy {
        (requireParentFragment() as GroupFragment).getSharedViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel.loadAllGroups()
    }

    override fun onResume() {
        super.onResume()

//        viewModel.loadAllGroups()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchGroupBinding.inflate(inflater, container, false)

        adapter = SearchGroupAdapter(
            emptyList(), // groupList 초기화
            emptyList(), // joinedGroupList 초기화
        ){ group ->
            viewModel.joinGroup(false, group.id)
        }

        binding.recyclerViewMyGroup.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyGroup.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            handleLoadingState(isLoading)
        }

        binding.searchEditText.doAfterTextChanged {
            val query = it.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchGroups(query)
            } else {
                viewModel.loadAllGroups()
            }
        }

        viewModel.myGroups.observe(viewLifecycleOwner) { groups ->
            adapter.updateData(viewModel.groupsAll.value ?: emptyList(), groups)
        }

        viewModel.groupsAll.observe(viewLifecycleOwner) { groups ->
            if (groups.isEmpty()) {
                binding.recyclerViewMyGroup.visibility = View.GONE
                binding.tvEmptyGroupMessage.visibility = View.VISIBLE
            } else {
                binding.recyclerViewMyGroup.visibility = View.VISIBLE
                binding.tvEmptyGroupMessage.visibility = View.GONE
                adapter.updateData(groups, viewModel.myGroups.value ?: emptyList())
            }
        }

        return binding.root
    }

    private fun handleLoadingState(isLoading: Boolean) {
        binding.llLoading.isVisible = isLoading
    }
}