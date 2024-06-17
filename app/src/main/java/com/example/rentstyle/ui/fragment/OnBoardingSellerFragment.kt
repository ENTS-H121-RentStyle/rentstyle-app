package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rentstyle.databinding.FragmentOnBoardingSellerBinding
import com.example.rentstyle.helpers.adapter.OnBoardingSellerAdapter

class OnBoardingSellerFragment : Fragment() {
    private lateinit var _binding: FragmentOnBoardingSellerBinding
    private val binding get() = _binding

    private lateinit var vpAdapter: OnBoardingSellerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingSellerBinding.inflate(inflater, container, false)

        vpAdapter = OnBoardingSellerAdapter(childFragmentManager, lifecycle)
        binding.vpWelcomeScreenSeller.adapter = vpAdapter

        return binding.root
    }
}