package com.example.rentstyle.helpers.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rentstyle.ui.fragment.SellerRegisterFragment
import com.example.rentstyle.ui.fragment.WelcomeSellerFragment

class OnBoardingSellerAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                WelcomeSellerFragment()
            }
            else -> {
                SellerRegisterFragment()
            }
        }
    }
}