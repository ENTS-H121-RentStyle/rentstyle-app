package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.rentstyle.databinding.FragmentHomeBinding
import com.example.rentstyle.helpers.adapter.HomeFragmentPageAdapter
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment(){
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    private lateinit var tabLayout : TabLayout
    private lateinit var viewPager : ViewPager2
    private lateinit var adapter: HomeFragmentPageAdapter

    private lateinit var shoppingCart : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            tabLayout = tlNavigationHeader
            viewPager = vpNavigationHeader
            shoppingCart = btnShoppingCart
        }

        tabLayout.addTab(tabLayout.newTab().setText("For You"))
        tabLayout.addTab(tabLayout.newTab().setText("Favorite"))

        adapter = HomeFragmentPageAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null){
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        shoppingCart.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationShoppingCart())
        }

        return binding.root
    }
}