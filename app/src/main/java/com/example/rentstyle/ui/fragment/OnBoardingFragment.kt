package com.example.rentstyle.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentOnBoardingBinding
import com.example.rentstyle.helpers.adapter.OnBoardingAdapter

class OnBoardingFragment : Fragment() {
    private lateinit var _binding: FragmentOnBoardingBinding
    private val binding get() = _binding

    private lateinit var vpAdapter: OnBoardingAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)

        vpAdapter = OnBoardingAdapter(childFragmentManager, lifecycle)
        binding.vpWelcomeScreen.adapter = vpAdapter

        return binding.root
    }
}