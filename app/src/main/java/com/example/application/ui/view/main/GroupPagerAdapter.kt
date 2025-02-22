package com.example.application.ui.view.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.application.ui.view.main.pages.group.MyGroupFragment
import com.example.application.ui.view.main.pages.group.SearchGroupFragment

class GroupPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyGroupFragment()
            1 -> SearchGroupFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}